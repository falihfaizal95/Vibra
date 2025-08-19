#!/bin/bash

echo "🎨 Gallery App - Local Deployment"
echo "=================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or later."
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or later is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven."
    echo "   On macOS: brew install maven"
    echo "   On Ubuntu: sudo apt install maven"
    exit 1
fi

echo "✅ Maven version: $(mvn -version | head -n 1)"

echo ""
echo "🔧 Building the application..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "🚀 Starting Gallery App..."
    echo "   - Search for artists, albums, movies, and more"
    echo "   - Use the Play button to start random image rotation"
    echo "   - Press Ctrl+C to exit"
    echo ""
    
    mvn javafx:run
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
