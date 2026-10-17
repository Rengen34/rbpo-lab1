@echo off
setlocal
cd /d "%~dp0"

for /f "delims=" %%J in ('powershell -NoProfile -Command "[Environment]::GetEnvironmentVariable('JAVA_HOME','User')"') do set "JAVA_HOME=%%J"
if exist "%JAVA_HOME%\bin\java.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>&1
if errorlevel 1 (
    echo Java was not found. Install JDK 17 or newer, then try again.
    pause
    exit /b 1
)

echo Wait for "Started LabApplication", then open:
echo http://127.0.0.1:8080/api/message
echo http://127.0.0.1:8080/api/numbers
echo.
call mvnw.cmd -q spring-boot:run

echo.
echo The server has stopped. Press any key to close this window.
pause >nul
