@echo off
REM Execution script for Desktop Text Editor with Auto-Save
REM Ensure PATH_TO_FX points to your JavaFX lib directory (e.g. C:\javafx-sdk-23\lib)

if "%PATH_TO_FX%"=="" (
    echo [WARNING] PATH_TO_FX environment variable is not set.
    echo Please set PATH_TO_FX to your JavaFX SDK lib folder, e.g.:
    echo   set PATH_TO_FX="C:\path\to\javafx-sdk\lib"
    pause
    exit /b 1
)

if not exist bin\TextEditorApp.class (
    echo Please run compile.bat first!
    pause
    exit /b 1
)

echo Starting Text Editor...
java --module-path %PATH_TO_FX% --add-modules javafx.controls -cp bin TextEditorApp

