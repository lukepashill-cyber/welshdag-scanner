#!/bin/bash
# WelshDAG Scanner - Automatic APK Builder

echo "================================"
echo "  WelshDAG Scanner APK Builder"
echo "================================"
echo ""

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install JDK 17+"
    echo "   Download: https://www.oracle.com/java/technologies/downloads/"
    exit 1
fi

if [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "❌ ANDROID_SDK_ROOT not set"
    echo "   Set it with: export ANDROID_SDK_ROOT=/path/to/android/sdk"
    exit 1
fi

echo "✅ Java: $(java -version 2>&1 | head -n1)"
echo "✅ Android SDK: $ANDROID_SDK_ROOT"
echo ""

# Build
echo "Building APK..."
echo ""

./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "================================"
    echo "  ✅ BUILD SUCCESSFUL!"
    echo "================================"
    echo ""
    echo "APK location:"
    echo "  $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "Size: $(du -h app/build/outputs/apk/debug/app-debug.apk | cut -f1)"
    echo ""
    echo "Install with:"
    echo "  adb install -r app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo "❌ Build failed. Check errors above."
    exit 1
fi
