"""Check that the concept-stage design document has actionable sections."""

from pathlib import Path
import sys


REQUIRED_SECTIONS = (
    "## 1. 目标",
    "## 3. 团队结构",
    "## 4. 最小系统边界",
    "## 5. 风险",
    "## 6. 测试策略",
    "## 8. 验收标准",
)


def main() -> int:
    document = Path(__file__).resolve().parents[1] / "DESIGN_DOCUMENT.md"
    content = document.read_text(encoding="utf-8")
    missing = [section for section in REQUIRED_SECTIONS if section not in content]
    if missing:
        print("设计文档检查失败，缺少：" + "、".join(missing))
        return 1
    print(f"设计文档检查通过：{document.name}，{len(REQUIRED_SECTIONS)} 个必需部分齐全")
    return 0


if __name__ == "__main__":
    sys.exit(main())

