#!/bin/bash
# ================================================================
# compile.sh - Compiles the entire project
# Usage: bash compile.sh
# ================================================================

echo "Creating output directory..."
mkdir -p out

echo "Compiling Java sources..."
javac -cp "lib/mysql-connector-j-9.7.0.jar" \
      -sourcepath src \
      -d out \
      src/database/DatabaseConnection.java \
      src/models/Admin.java \
      src/models/Record.java \
      src/models/ValidationLog.java \
      src/models/DuplicateLog.java \
      src/utility/LevenshteinDistanceUtil.java \
      src/dao/AdminDAO.java \
      src/dao/RecordDAO.java \
      src/dao/ValidationLogDAO.java \
      src/dao/DuplicateLogDAO.java \
      src/service/ValidationService.java \
      src/service/DuplicateDetectionService.java \
      src/service/ReportService.java \
      src/ui/SimpleHttpServer.java \
      src/ui/Main.java

if [ $? -eq 0 ]; then
  echo "✅ Compilation successful!"
else
  echo "❌ Compilation failed. Check errors above."
fi
