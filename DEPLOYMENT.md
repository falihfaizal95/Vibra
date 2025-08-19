# 🚀 Gallery App - Local Deployment Guide

This guide will help you deploy the Gallery App locally on your machine.

## 📋 Prerequisites

### Required Software
- **Java 17 or later** (JDK, not just JRE)
- **Maven 3.6+** (for dependency management and building)
- **Internet connection** (for downloading dependencies and accessing iTunes API)

### System Requirements
- **Operating System**: Windows, macOS, or Linux
- **Memory**: At least 2GB RAM (4GB recommended)
- **Storage**: 100MB free space
- **Display**: 1024x768 minimum resolution

## 🔧 Installation Steps

### 1. Install Java 17+

#### On macOS:
```bash
# Using Homebrew (recommended)
brew install openjdk@17

# Or download from Oracle
# Visit: https://www.oracle.com/java/technologies/downloads/
```

#### On Windows:
1. Download OpenJDK 17 from: https://adoptium.net/
2. Run the installer and follow the setup wizard
3. Add Java to your PATH environment variable

#### On Ubuntu/Debian:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### 2. Install Maven

#### On macOS:
```bash
brew install maven
```

#### On Windows:
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to a directory (e.g., `C:\Program Files\Apache\maven`)
3. Add Maven's bin directory to your PATH

#### On Ubuntu/Debian:
```bash
sudo apt install maven
```

### 3. Verify Installation

Open a terminal/command prompt and run:
```bash
java -version
mvn -version
```

You should see version information for both tools.

## 🚀 Quick Start

### Option 1: Using the provided scripts (Recommended)

#### On macOS/Linux:
```bash
./run.sh
```

#### On Windows:
```cmd
run.bat
```

### Option 2: Manual deployment

1. **Navigate to the project directory:**
   ```bash
   cd /path/to/Gallery-App
   ```

2. **Build the project:**
   ```bash
   mvn clean compile
   ```

3. **Run the application:**
   ```bash
   mvn javafx:run
   ```

## 🎯 Using the Application

### Basic Usage:
1. **Search**: Enter a search term (e.g., "Taylor Swift", "Avengers", "Podcast Name")
2. **Select Media Type**: Choose from the dropdown (music, movie, podcast, etc.)
3. **Get Images**: Click "Get Images" to search and display results
4. **Play Mode**: Click "Play" to start random image rotation every 2 seconds

### Features:
- **Grid Display**: Shows up to 20 images in a 5x4 grid
- **Random Rotation**: Automatically replaces images in play mode
- **Progress Tracking**: Visual progress bar during searches
- **Error Handling**: User-friendly error messages for failed searches

## 🔍 Troubleshooting

### Common Issues:

#### 1. "Java is not installed" error
- Ensure you have Java 17+ installed (not just JRE)
- Verify JAVA_HOME environment variable is set
- Try running `java -version` in terminal

#### 2. "Maven is not installed" error
- Install Maven using your system's package manager
- Ensure Maven is in your PATH
- Try running `mvn -version` in terminal

#### 3. Build failures
- Check internet connection (needed for downloading dependencies)
- Ensure you're in the correct project directory
- Try running `mvn clean` before building

#### 4. Application won't start
- Check if another instance is already running
- Verify firewall settings aren't blocking the app
- Check console output for specific error messages

#### 5. No images displayed
- Verify internet connection (needed for iTunes API)
- Check if search term returns results
- Try different search terms or media types

### Getting Help:
- Check the console output for detailed error messages
- Verify all prerequisites are installed correctly
- Try searching for different terms to test the API connection

## 📦 Building Executable JAR

To create a standalone executable JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target/` directory as `gallery-app-1.0.0.jar`.

To run the JAR:
```bash
java -jar target/gallery-app-1.0.0.jar
```

## 🔧 Advanced Configuration

### Customizing the Application:

1. **Change default search term**: Edit `GalleryApp.java` line 108
2. **Modify grid size**: Edit the grid creation logic in `displayImages()` method
3. **Adjust rotation speed**: Change the duration in `startPlayMode()` method

### Environment Variables:
- `JAVA_HOME`: Set to your Java installation directory
- `MAVEN_HOME`: Set to your Maven installation directory

## 📞 Support

If you encounter issues:
1. Check this troubleshooting guide
2. Verify all prerequisites are correctly installed
3. Check the console output for specific error messages
4. Try running with different search terms to isolate API issues

---

**Happy browsing! 🎨**
