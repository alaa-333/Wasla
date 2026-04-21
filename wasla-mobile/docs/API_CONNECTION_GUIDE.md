# API Connection Guide

Configure backend connection for all platforms.

## Configuration File

**Location**: `lib/core/config/app_config.dart`

```dart
class AppConfig {
  // Production URL
  static const String _productionUrl = 'https://wasla-api.up.railway.app';
  
  // Local development URLs
  static const String _localhostUrl = 'http://localhost:8080';
  static const String _androidEmulatorUrl = 'http://10.0.2.2:8080';
  
  // Physical device (uncomment and set your IP)
  // static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
  static const String? _physicalDeviceUrl = null;

  // Toggle: false = Local, true = Production
  static const bool _useProduction = false;
}
```

## Platform URLs

| Platform | URL | Auto-detected |
|----------|-----|---------------|
| **Web** | `http://localhost:8080` | ✅ |
| **Android Emulator** | `http://10.0.2.2:8080` | ✅ |
| **iOS Simulator** | `http://localhost:8080` | ✅ |
| **Physical Device** | Manual setup required | ⚠️ |

## Physical Device Setup

1. **Get your IP address:**
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```

2. **Update config:**
   ```dart
   static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
   ```

3. **Ensure backend listens on 0.0.0.0:**
   ```yaml
   # application.yml
   server:
     address: 0.0.0.0
     port: 8080
   ```

## Error Messages

The app now shows specific error messages:

| Error | Message |
|-------|---------|
| Backend offline | "لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend" |
| No internet | "لا يوجد اتصال بالإنترنت. تحقق من اتصالك" |
| Wrong credentials | "بيانات الدخول غير صحيحة" |
| Timeout | "انتهت مهلة الاتصال" |
| Server error | "خطأ في الخادم. يرجى المحاولة لاحقاً" |

## Production Deployment

Set production mode:
```dart
static const bool _useProduction = true;
```

## Troubleshooting

**Backend connection failed:**
1. Verify backend runs on `localhost:8080`
2. Check firewall settings
3. For Android emulator, use `10.0.2.2:8080`

**Test connection:**
```bash
curl http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```
