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

set "API_ROOT=http://127.0.0.1:8080/api"
echo Wait for "Started LabApplication". Replace {id} with a real number from POST.
echo The browser opens GET links. Use PowerShell for POST, PATCH, and DELETE.
echo.
echo Lab 1:
echo GET    %API_ROOT%/message
echo GET    %API_ROOT%/numbers
echo.
echo Lab 2 - users:
echo GET    %API_ROOT%/users
echo GET    %API_ROOT%/users/{id}
echo POST   %API_ROOT%/users
echo PATCH  %API_ROOT%/users/{id}
echo DELETE %API_ROOT%/users/{id}
echo.
echo Lab 2 - purchase requests:
echo GET    %API_ROOT%/purchase-requests
echo GET    %API_ROOT%/purchase-requests/{id}
echo POST   %API_ROOT%/purchase-requests
echo PATCH  %API_ROOT%/purchase-requests/{id}
echo DELETE %API_ROOT%/purchase-requests/{id}
echo.
echo Lab 2 - suppliers:
echo GET    %API_ROOT%/suppliers
echo GET    %API_ROOT%/suppliers/{id}
echo POST   %API_ROOT%/suppliers
echo PATCH  %API_ROOT%/suppliers/{id}
echo DELETE %API_ROOT%/suppliers/{id}
echo.
echo Lab 2 - commercial offers:
echo GET    %API_ROOT%/commercial-offers
echo GET    %API_ROOT%/commercial-offers/{id}
echo POST   %API_ROOT%/commercial-offers
echo PATCH  %API_ROOT%/commercial-offers/{id}
echo DELETE %API_ROOT%/commercial-offers/{id}
echo.
echo Lab 2 - purchase orders:
echo GET    %API_ROOT%/purchase-orders
echo GET    %API_ROOT%/purchase-orders/{id}
echo POST   %API_ROOT%/purchase-orders
echo PATCH  %API_ROOT%/purchase-orders/{id}
echo DELETE %API_ROOT%/purchase-orders/{id}
echo.
call mvnw.cmd -q spring-boot:run

echo.
echo The server has stopped. Press any key to close this window.
pause >nul
