@echo off
echo 🎨 Gallery App - Local Deployment
echo ==================================

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java is not installed. Please install Java 17 or later.
    pause
    exit /b 1
)

echo ✅ Java is installed

REM Check if Maven is installed
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven is not installed. Please install Maven.
    echo    Download from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo ✅ Maven is installed

echo.
echo 🔧 Building the application...
call mvn clean compile

if errorlevel 1 (
    echo ❌ Build failed. Please check the error messages above.
    pause
    exit /b 1
)

echo ✅ Build successful!
echo.
echo 🚀 Starting Gallery App...
echo    - Search for artists, albums, movies, and more
echo    - Use the Play button to start random image rotation
echo    - Close the window to exit
echo.

call mvn javafx:run

pause
