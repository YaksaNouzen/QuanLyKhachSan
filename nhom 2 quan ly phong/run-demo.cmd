@echo off
setlocal
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1"
if errorlevel 1 exit /b 1
java -Djava.awt.headless=false -cp "%~dp0build\classes" app.PhongMainView --demo
