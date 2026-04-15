# Troubleshooting

Common issues and solutions.

## VS Code Asset Warnings

**Issue:**
```
The asset directory 'assets/images/' doesn't exist.
The asset directory 'assets/icons/' doesn't exist.
```

**Solution:**
VS Code cache issue. Reload the window:
1. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
2. Type "Reload Window"
3. Select "Developer: Reload Window"

**Note:** The directories exist and the app works fine. This is just a VS Code display issue.

## Backend Connection Issues

**Issue:** "لا يمكن الاتصال بالخادم"

**Solutions:**
1. **Start backend:**
   ```bash
   # Make sure backend runs on localhost:8080
   ./mvnw spring-boot:run
   ```

2. **Check platform URL:**
   - Web: `http://localhost:8080` ✅
   - Android Emulator: `http://10.0.2.2:8080` ✅
   - Physical Device: Set your IP in `app_config.dart`

3. **Test connection:**
   ```bash
   curl http://localhost:8080/api/v1/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"test@example.com","password":"password123"}'
   ```

## Build Issues

**Issue:** Build failed

**Solution:**
```bash
flutter clean
flutter pub get
flutter run
```

## Login Issues

**Issue:** "بيانات الدخول غير صحيحة"

**Solutions:**
1. Check user exists in database
2. Verify email and password are correct
3. Check backend logs for errors

## Physical Device Connection

**Issue:** App can't connect from physical device

**Solutions:**
1. **Get your IP:**
   ```bash
   # Windows
   ipconfig
   
   # Mac/Linux
   ifconfig
   ```

2. **Update config:**
   ```dart
   // In lib/core/config/app_config.dart
   static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
   ```

3. **Ensure backend listens on 0.0.0.0:**
   ```yaml
   # application.yml
   server:
     address: 0.0.0.0
     port: 8080
   ```

## Common Error Messages

| Error | Meaning | Solution |
|-------|---------|----------|
| "لا يمكن الاتصال بالخادم" | Backend offline | Start backend |
| "لا يوجد اتصال بالإنترنت" | No internet | Check connection |
| "بيانات الدخول غير صحيحة" | Wrong credentials | Check email/password |
| "انتهت مهلة الاتصال" | Timeout | Check network/backend |

## Project Rules Reminder

- ✅ Text-only (no image upload)
- ✅ Bidding-only (no online payment)  
- ✅ Arabic UI
- ✅ Spring Boot backend (not Firebase)
