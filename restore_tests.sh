#!/bin/bash
# Restore test files from backup
mv test_backup/PostgresTableOperationsTest.kt src/test/kotlin/org/blackerp/infrastructure/persistence/ 2>/dev/null || true
mv test_backup/PluginSystemIntegrationTest.kt src/test/kotlin/org/blackerp/plugin/integration/ 2>/dev/null || true
mv test_backup/TableExtensionIntegrationTest.kt src/test/kotlin/org/blackerp/plugin/integration/ 2>/dev/null || true
rmdir test_backup 2>/dev/null || true
