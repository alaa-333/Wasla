# Quick Start Guide

Get the Wasla app running in 5 minutes.

## Setup

1. **Install dependencies**
   ```bash
   flutter pub get
   ```

2. **Start backend**
   ```bash
   # Make sure Spring Boot backend runs on:
   # http://localhost:8080
   ```

3. **Run app**
   ```bash
   flutter run
   ```

## Test Flows

**Client Flow:**
1. Select "عميل (Client)"
2. Register → Create job → Accept bid → Track delivery

**Driver Flow:**
1. Select "سائق (Driver)" 
2. Register → Browse jobs → Submit bid

## Key Commands

```bash
# Development
flutter run
flutter analyze
flutter format lib/

# Build
flutter build apk --release
flutter build ios --release

# Clean
flutter clean && flutter pub get
```

## Configuration

- **Backend URL**: `lib/core/config/app_config.dart`
- **App Colors**: `lib/core/theme/app_colors.dart`
- **Routes**: `lib/main.dart`

## Troubleshooting

- **Backend connection**: Check `docs/API_CONNECTION_GUIDE.md`
- **Build issues**: Run `flutter clean && flutter pub get`
- **Common problems**: Check `docs/TROUBLESHOOTING.md`

## Project Rules

- ✅ Text-only (no image upload)
- ✅ Bidding-only (no online payment)
- ✅ Arabic UI
- ✅ Spring Boot backend (not Firebase)
