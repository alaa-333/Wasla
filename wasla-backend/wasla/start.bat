@echo off
setlocal
cd /d "%~dp0"

echo [1/3] Starting Docker containers (PostgreSQL, Redis, pgAdmin)...
docker-compose up -d

echo.
echo [2/3] Waiting for containers to initialize (10 seconds)...
timeout /t 10 /nobreak >nul

echo.
echo [3/3] Starting Spring Boot application...
call .\mvnw.cmd spring-boot:run

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application failed to start.
    pause
    exit /b %ERRORLEVEL%
)

endlocal
