import json
import tempfile
import unittest
from pathlib import Path

import run_tests


class TestManifestSelection(unittest.TestCase):

    def setUp(self):
        self.targets = [
            {"id": "order", "size": "small", "module": "order-service", "command": "echo order", "inputs": []},
            {"id": "order-app", "size": "medium", "module": "order-app", "command": "echo app", "inputs": []},
            {"id": "addurl", "size": "large", "module": "addurl-workflow", "command": "echo addurl", "inputs": []},
        ]

    def test_order_service_change_selects_order_targets(self):
        selected, reason = run_tests.choose_affected_targets(
            self.targets, ["chapters/ch02/2.1.1-set-work/order-service/src/main/OrderService.java"]
        )

        self.assertEqual({"order", "order-app"}, {target["id"] for target in selected})
        self.assertIn("受影响", reason)

    def test_unknown_change_falls_back_to_all(self):
        selected, reason = run_tests.choose_affected_targets(self.targets, ["docs/new-note.txt"])

        self.assertEqual(3, len(selected))
        self.assertIn("全量", reason)

    def test_manifest_can_be_loaded(self):
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "manifest.json"
            path.write_text(json.dumps({"targets": self.targets}), encoding="utf-8")

            self.assertEqual(3, len(run_tests.load_manifest(path)))


class TestReports(unittest.TestCase):

    def test_isolation_scan_detects_fixed_port(self):
        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            source = root / "Sample.java"
            source.write_text('var endpoint = "http://localhost:8080";', encoding="utf-8")
            violations = run_tests.run_isolation_scan(root, [{"inputs": ["Sample.java"]}])

            self.assertIn("Sample.java", violations)


if __name__ == "__main__":
    unittest.main()
