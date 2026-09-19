#!/usr/bin/env python3
"""轻量教学测试平台：调度、受影响测试选择、隔离检查和报告生成。"""

from __future__ import annotations

import argparse
import json
import os
import random
import re
import subprocess
import sys
import tempfile
import time
import xml.etree.ElementTree as ET
from dataclasses import dataclass, asdict
from datetime import datetime, timezone
from pathlib import Path
from typing import Iterable


SIZE_TIMEOUT_SECONDS = {"small": 180, "medium": 300, "large": 480}
REPORT_DIRECTORY = Path("target/test-platform-reports")
FIXED_RESOURCE_PATTERNS = (
    re.compile(r"localhost:8080", re.IGNORECASE),
    re.compile(r"127\.0\.0\.1:8080", re.IGNORECASE),
    re.compile(r"Path\.of\(\s*[\"'](?:C:|D:|/tmp/)", re.IGNORECASE),
)


@dataclass
class TargetResult:
    id: str
    size: str
    module: str
    command: str
    status: str
    exit_code: int | None
    duration_seconds: float
    output_file: str
    inputs: list[str]
    isolation_violations: list[str]


def repository_root() -> Path:
    return Path(__file__).resolve().parents[2]


def load_manifest(path: Path) -> list[dict]:
    document = json.loads(path.read_text(encoding="utf-8"))
    targets = document.get("targets")
    if not isinstance(targets, list) or not targets:
        raise ValueError("test-manifest.json 必须包含非空 targets 数组")
    required = {"id", "size", "module", "command", "inputs"}
    for target in targets:
        missing = required - target.keys()
        if missing:
            raise ValueError(f"测试目标 {target.get('id')} 缺少字段：{sorted(missing)}")
        if target["size"] not in SIZE_TIMEOUT_SECONDS:
            raise ValueError(f"测试目标 {target['id']} 的规模无效：{target['size']}")
    return targets


def git_changed_files(root: Path, base: str) -> list[str]:
    commands = [["git", "diff", "--name-only", f"{base}...HEAD"], ["git", "diff", "--name-only", base], ["git", "diff", "--name-only"]]
    collected: list[str] = []
    for command in commands:
        completed = subprocess.run(command, cwd=root, text=True, capture_output=True)
        if completed.returncode == 0:
            collected.extend(line.strip().replace("\\", "/") for line in completed.stdout.splitlines() if line.strip())
            if collected:
                return sorted(set(collected))
        else:
            last_error = completed.stderr.strip()
    if collected:
        return sorted(set(collected))
    if 'last_error' in locals() and last_error:
        raise RuntimeError(f"无法计算相对于 {base} 的变更：{last_error}")
    return []


def choose_affected_targets(targets: list[dict], changed_files: list[str]) -> tuple[list[dict], str]:
    if not changed_files:
        return targets, "未检测到变更，安全运行全量测试"
    normalized = [path.replace("\\", "/") for path in changed_files]
    platform_change = any(
        path == "test-manifest.json" or path.startswith("tools/test-platform/")
        for path in normalized
    )
    shared_change = any(
        path.endswith("/pom.xml") and ("2.1.1-set-work/pom.xml" in path or "2.1.9-addurl-workflow/pom.xml" in path)
        or "service-contracts/" in path
        or path == "pom.xml"
        for path in normalized
    )
    if platform_change or shared_change:
        reason = "测试平台、清单、父 POM 或公共契约发生变化，降级为全量测试"
        return targets, reason

    selected_ids: set[str] = set()
    unknown = False
    for path in normalized:
        if "2.1.1-set-work/order-service/" in path:
            selected_ids.update(target["id"] for target in targets if target["module"] in {"order-service", "order-app"})
        elif "2.1.1-set-work/fake-services/" in path:
            selected_ids.update(target["id"] for target in targets if target["module"] in {"fake-services", "order-app"})
        elif "2.1.1-set-work/order-app/" in path:
            selected_ids.update(target["id"] for target in targets if target["module"] == "order-app")
        elif "2.1.2-2.1.8-set-foundations/" in path:
            selected_ids.update(target["id"] for target in targets if target["module"] == "set-foundations")
        elif "2.1.9-addurl-workflow/" in path:
            if "/frontend/" in path:
                selected_ids.update(
                    target["id"] for target in targets
                    if target["module"] in {"addurl-frontend", "addurl-workflow"}
                )
            elif "/service/" in path or "/proto/" in path:
                selected_ids.update(
                    target["id"] for target in targets
                    if target["module"] in {"addurl-service", "addurl-protocol", "addurl-workflow"}
                )
            else:
                selected_ids.update(target["id"] for target in targets if target["module"] == "addurl-workflow")
        elif path in {"README.md"} or path.endswith(".md"):
            continue
        else:
            unknown = True

    if unknown or not selected_ids:
        return targets, "无法安全判断影响范围，降级为全量测试"
    selected = [target for target in targets if target["id"] in selected_ids]
    return selected, f"根据 {len(normalized)} 个变更文件选择 {len(selected)} 个受影响目标"


def run_isolation_scan(root: Path, targets: Iterable[dict]) -> dict[str, list[str]]:
    violations: dict[str, list[str]] = {}
    candidates: set[str] = set()
    for target in targets:
        candidates.update(target.get("inputs", []))
    for relative in candidates:
        path = root / relative
        files = [path] if path.is_file() else list(path.rglob("*.java")) if path.exists() else []
        for file_path in files:
            try:
                content = file_path.read_text(encoding="utf-8")
            except (OSError, UnicodeDecodeError):
                continue
            for pattern in FIXED_RESOURCE_PATTERNS:
                if pattern.search(content):
                    violations.setdefault(str(file_path.relative_to(root)), []).append(
                        f"命中固定资源模式：{pattern.pattern}"
                    )
    return violations


def execute_target(root: Path, target: dict, output_dir: Path, isolation: bool) -> TargetResult:
    started = time.perf_counter()
    output_path = output_dir / f"{target['id']}.log"
    environment = os.environ.copy()
    with tempfile.TemporaryDirectory(prefix="google-testing-") as temporary_directory:
        environment["TEST_PLATFORM_TEMP_DIR"] = temporary_directory
        try:
            completed = subprocess.run(
                target["command"],
                cwd=root,
                shell=True,
                text=True,
                capture_output=True,
                timeout=SIZE_TIMEOUT_SECONDS[target["size"]],
                env=environment,
            )
            exit_code = completed.returncode
            status = "通过" if exit_code == 0 else "失败"
            output = completed.stdout + "\n" + completed.stderr
        except subprocess.TimeoutExpired as exception:
            exit_code = None
            status = "超时"
            stdout = exception.stdout or ""
            stderr = exception.stderr or ""
            if isinstance(stdout, bytes):
                stdout = stdout.decode("utf-8", errors="replace")
            if isinstance(stderr, bytes):
                stderr = stderr.decode("utf-8", errors="replace")
            output = stdout + "\n" + stderr
        output_path.write_text(output, encoding="utf-8", errors="replace")
    duration = round(time.perf_counter() - started, 3)
    isolation_violations = []
    if isolation and "8080" in output:
        isolation_violations.append("输出中出现固定端口 8080")
        if status == "通过":
            status = "隔离失败"
    return TargetResult(
        id=target["id"],
        size=target["size"],
        module=target["module"],
        command=target["command"],
        status=status,
        exit_code=exit_code,
        duration_seconds=duration,
        output_file=str(output_path),
        inputs=target.get("inputs", []),
        isolation_violations=isolation_violations,
    )


def write_reports(root: Path, results: list[TargetResult], selection_reason: str, output_dir: Path) -> None:
    payload = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "selection_reason": selection_reason,
        "summary": {
            "total": len(results),
            "passed": sum(result.status == "通过" for result in results),
            "failed": sum(result.status not in {"通过"} for result in results),
            "duration_seconds": round(sum(result.duration_seconds for result in results), 3),
        },
        "results": [asdict(result) for result in results],
    }
    (output_dir / "test-results.json").write_text(
        json.dumps(payload, ensure_ascii=False, indent=2), encoding="utf-8"
    )

    lines = ["# 测试平台汇总", "", f"选择说明：{selection_reason}", "", "| 目标 | 规模 | 状态 | 耗时（秒） |", "| --- | --- | --- | ---: |"]
    for result in results:
        lines.append(f"| `{result.id}` | {result.size} | {result.status} | {result.duration_seconds:.3f} |")
    lines.extend(["", "## 统计", "", f"- 总目标：{len(results)}", f"- 通过：{sum(result.status == '通过' for result in results)}", f"- 非通过：{sum(result.status != '通过' for result in results)}"])
    (output_dir / "summary.md").write_text("\n".join(lines) + "\n", encoding="utf-8")

    suite = ET.Element("testsuite", name="GoogleTestingPlatform", tests=str(len(results)))
    for result in results:
        case = ET.SubElement(suite, "testcase", name=result.id, classname=result.module, time=str(result.duration_seconds))
        if result.status != "通过":
            failure = ET.SubElement(case, "failure", message=result.status)
            failure.text = f"退出码：{result.exit_code}; 日志：{result.output_file}"
    ET.ElementTree(suite).write(output_dir / "junit.xml", encoding="utf-8", xml_declaration=True)


def parse_arguments(argv: list[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="运行教学项目的分层测试目标")
    selection = parser.add_mutually_exclusive_group(required=False)
    selection.add_argument("--size", choices=["small", "medium", "large"], help="只运行一种规模")
    selection.add_argument("--all", action="store_true", help="运行全部规模")
    selection.add_argument("--affected", action="store_true", help="根据 git diff 运行受影响测试")
    parser.add_argument("--base", default="origin/main", help="受影响测试的比较基线")
    parser.add_argument("--manifest", default="test-manifest.json", help="测试清单路径")
    parser.add_argument("--output-dir", default=str(REPORT_DIRECTORY), help="报告目录")
    parser.add_argument("--random-order", "--random", action="store_true", help="随机化执行顺序")
    parser.add_argument("--seed", type=int, help="随机顺序种子")
    parser.add_argument("--isolation", action="store_true", help="执行固定资源静态检查和临时目录隔离")
    parser.add_argument("--dry-run", action="store_true", help="只打印目标，不执行 Maven")
    return parser.parse_args(argv)


def main() -> int:
    arguments = parse_arguments()
    root = repository_root()
    manifest_path = root / arguments.manifest
    output_dir = root / arguments.output_dir
    output_dir.mkdir(parents=True, exist_ok=True)
    targets = load_manifest(manifest_path)
    selection_reason = "按命令行选择测试目标"
    if arguments.size:
        targets = [target for target in targets if target["size"] == arguments.size]
        selection_reason = f"只选择 {arguments.size} 规模目标"
    elif arguments.affected:
        changed_files = git_changed_files(root, arguments.base)
        targets, selection_reason = choose_affected_targets(targets, changed_files)
    else:
        selection_reason = "未指定规模，默认运行全量测试"

    static_violations = run_isolation_scan(root, targets) if arguments.isolation else {}
    if static_violations:
        print("隔离检查发现固定资源引用：", file=sys.stderr)
        for file_name, messages in static_violations.items():
            print(f"- {file_name}: {'; '.join(messages)}", file=sys.stderr)
        return 2

    if arguments.random_order:
        randomizer = random.Random(arguments.seed)
        randomizer.shuffle(targets)
        selection_reason += f"；随机顺序 seed={arguments.seed if arguments.seed is not None else '系统随机'}"

    print(f"测试目标数量：{len(targets)}（{selection_reason}）")
    if arguments.dry_run:
        for target in targets:
            print(f"- [{target['size']}] {target['id']}: {target['command']}")
        return 0

    results: list[TargetResult] = []
    for index, target in enumerate(targets, start=1):
        print(f"[{index}/{len(targets)}] 运行 {target['id']}（{target['size']}）")
        result = execute_target(root, target, output_dir, arguments.isolation)
        results.append(result)
        print(f"    {result.status}，耗时 {result.duration_seconds:.3f} 秒")
        if result.status != "通过":
            print(f"    日志：{result.output_file}")
    write_reports(root, results, selection_reason, output_dir)
    failed = [result for result in results if result.status != "通过"]
    print(f"报告：{output_dir / 'summary.md'}")
    return 1 if failed else 0


if __name__ == "__main__":
    raise SystemExit(main())
