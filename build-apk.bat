@echo off
REM WelshDAG Scanner - Automatic APK Builder (Windows)

echo ================================
echo   WelshDAG Scanner APK Builder
echo ================================
echo.

REM Check Java
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java not found
    echo Download JDK 17+ from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

echo Checking prerequisites...

java -version

if "%ANDROID_SDK_ROOT%"=="" (
    echo Error: ANDROID_SDK_ROOT not set
    echo Set it with: setx ANDROID_SDK_ROOT C:\path\to\android\sdk
    pause
    exit /b 1
)

echo Android SDK: %ANDROID_SDK_ROOT%
echo.

echo Building APK...
echo.

call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ================================
    echo   BUILD SUCCESSFUL!
    echo ================================
    echo.
    echo APK location:
    echo   %cd%\app\build\outputs\apk\debug\app-debug.apk
    echo.
    echo Install with:
    echo   adb install -r app\build\outputs\apk\debug\app-debug.apk
    echo.
    pause
) else (
    echo.
    echo Build failed. Check errors above.
    pause
    exit /b 1
)
