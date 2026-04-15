# Quick Start Guide - Wasla Project

**For Developers** | **Last Updated**: April 15, 2026

---

## 🚀 Get Started in 5 Minutes

### 1. Install Dependencies
```bash
flutter pub get
```

### 2. Start Backend
Make sure your Spring Boot backend is running on:
```
http://localhost:8080
```

### 3. Run the App
```bash
flutter run
```

That's it! 🎉

---

## 📱 Test the App

### Test as Client
1. Open app → Select "عميل (Client)"
2. Register with test data
3. Create a job with pickup/dropoff locations
4. Wait for driver bids
5. Accept a bid and track delivery

### Test as Driver
1. Open app → Select "سائق (Driver)"
2. Register with vehicle details
3. Browse available jobs
4. Submit a bid with your price
5. Wait for client acceptance

---

## 🔧 Common Commands

### Development
```bash
# Run with hot reload
flutter run

# Run on specific device
flutter run -d <device-id>

# Check for issues
flutter analyze

# Format code
flutter format lib/
```

### Building
```bash
# Debug build
flutter build apk --debug

# Release build
flutter build apk --release

# iOS build
flutter build ios --release
```

### Cleaning
```bash
# Clean build files
flutter clean

# Reinstall dependencies
flutter pub get
```

---

## 📂 Key Files to Know

### Configuration
- `pubspec.yaml` - Dependencies and assets
- `lib/core/api/api_client.dart` - Backend URL configuration
- `lib/core/constants/app_constants.dart` - App constants

### Entry Point
- `lib/main.dart` - App entry point and routes

### Screens
- `lib/presentation/screens/auth/` - Login, registration
- `lib/presentation/screens/client/` - Client screens
- `lib/presentation/screens/driver/` - Driver screens

### Services
- `lib/data/services/auth_service.dart` - Authentication
- `lib/data/services/job_service.dart` - Job management
- `lib/data/services/bid_service.dart` - Bidding system

---

## 🎨 Customization

### Change Backend URL
**File**: `lib/core/api/api_client.dart`
```dart
static const String baseUrl = 'http://your-backend-url/api/v1';
```

### Change App Colors
**File**: `lib/core/theme/app_colors.dart`
```dart
static const Color primary = Color(0xFF2196F3);
static const Color secondary = Color(0xFFFF9800);
```

### Change App Name
**File**: `lib/main.dart`
```dart
title: 'Your App Name',
```

---

## 🐛 Troubleshooting

### Issue: "Asset directory doesn't exist"
**Solution**: Already fixed! Folders created with `.gitkeep` files.

### Issue: "Package not found"
**Solution**: Run `flutter pub get`

### Issue: "Build failed"
**Solution**: 
```bash
flutter clean
flutter pub get
flutter run
```

### Issue: "Backend connection failed"
**Solution**: 
1. Check backend is running on `localhost:8080`
2. Check `api_client.dart` has correct URL
3. For Android emulator, use `10.0.2.2:8080` instead of `localhost:8080`

---

## 📚 Documentation

- `PROJECT_STATUS.md` - Overall project status
- `FLUTTER_FIXES_REPORT.md` - All fixes applied
- `API_DOCUMENTATION.md` - Backend API reference
- `UI_DOCUMENTATION.md` - UI components guide
- `TROUBLESHOOTING.md` - Common issues

---

## ✅ Verification Checklist

Before committing code:
- [ ] Run `flutter analyze` - Should show "No issues found!"
- [ ] Run `flutter format lib/` - Format all code
- [ ] Test on Android device/emulator
- [ ] Test on iOS device/simulator (if available)
- [ ] Verify backend connectivity
- [ ] Test both client and driver flows

---

## 🎯 Project Rules

### ❌ Don't Do
- Don't add payment systems (bidding only)
- Don't add image upload (text only)
- Don't use Firebase (Spring Boot backend)
- Don't mix English in UI (Arabic only)

### ✅ Do
- Use Clean Architecture
- Add proper error handling
- Use Arabic for all UI text
- Follow existing code style
- Add mounted checks for async operations

---

## 🚦 Current Status

✅ **All Issues Fixed**  
✅ **Production Ready**  
✅ **Flutter Analyze: No issues found!**

---

## 📞 Need Help?

Check the documentation in `docs/` folder:
- For API issues → `API_DOCUMENTATION.md`
- For UI issues → `UI_DOCUMENTATION.md`
- For errors → `TROUBLESHOOTING.md`
- For project info → `PROJECT_STATUS.md`

---

**Happy Coding! 🚀**
