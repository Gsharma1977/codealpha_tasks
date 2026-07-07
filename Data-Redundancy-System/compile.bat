@echo off
echo Creating output directory...
if not exist out mkdir out

echo Compiling Java sources...
javac -cp "lib\mysql-connector-j-8.x.x.jar" ^
      -sourcepath src ^
      -d out ^
      src\database\DatabaseConnection.java ^
      src\models\Admin.java ^
      src\models\Record.java ^
      src\models\ValidationLog.java ^
      src\models\DuplicateLog.java ^
      src\utility\LevenshteinDistanceUtil.java ^
      src\dao\AdminDAO.java ^
      src\dao\RecordDAO.java ^
      src\dao\ValidationLogDAO.java ^
      src\dao\DuplicateLogDAO.java ^
      src\service\ValidationService.java ^
      src\service\DuplicateDetectionService.java ^
      src\service\ReportService.java ^
      src\ui\SimpleHttpServer.java ^
      src\ui\Main.java

if %ERRORLEVEL% == 0 (
  echo [SUCCESS] Compilation successful!
) else (
  echo [ERROR] Compilation failed.
)
