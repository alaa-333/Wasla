# دليل الاتصال بالـ API - Wasla Project

**Last Updated**: April 15, 2026

---

## 🎯 ملخص سريع

تم إصلاح جميع مشاكل الاتصال بالـ API:
- ✅ إصلاح عنوان الـ API حسب المنصة
- ✅ معالجة صحيحة للأخطاء (لا مزيد من "No Internet" الخاطئة)
- ✅ تسجيل دخول يعمل بشكل صحيح
- ✅ رسائل خطأ واضحة ومفيدة

---

## 🔧 إعداد الـ Backend URL

### الملف الرئيسي: `lib/core/config/app_config.dart`

```dart
class AppConfig {
  // 🔴 للإنتاج (Production)
  static const String _productionUrl = 'https://wasla-api.up.railway.app';
  
  // 🟢 للتطوير المحلي (Local Development)
  static const String _localhostUrl = 'http://localhost:8080';
  static const String _androidEmulatorUrl = 'http://10.0.2.2:8080';
  
  // 📱 للجهاز الفعلي (Physical Device)
  // قم بإلغاء التعليق وضع IP الخاص بك
  // static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
  static const String? _physicalDeviceUrl = null;

  // ⚙️ تبديل بين Production و Development
  static const bool _useProduction = false; // ✅ false = Local, true = Production
}
```

---

## 📱 حسب المنصة

### 1. **Chrome / Web** 🌐
```
✅ يستخدم تلقائياً: http://localhost:8080
```
لا حاجة لتغيير أي شيء!

### 2. **Android Emulator** 📱
```
✅ يستخدم تلقائياً: http://10.0.2.2:8080
```
`10.0.2.2` هو عنوان خاص يشير إلى `localhost` من داخل الـ Emulator.

### 3. **iOS Simulator** 🍎
```
✅ يستخدم تلقائياً: http://localhost:8080
```
iOS Simulator يمكنه الوصول مباشرة إلى localhost.

### 4. **Physical Device** 📲
```
⚠️ يحتاج إعداد يدوي!
```

**خطوات الإعداد:**

#### أ. احصل على IP الخاص بجهازك:

**Windows:**
```bash
ipconfig
# ابحث عن: IPv4 Address
# مثال: 192.168.1.100
```

**Mac/Linux:**
```bash
ifconfig
# أو
ip addr show
# ابحث عن: inet
# مثال: 192.168.1.100
```

#### ب. افتح `lib/core/config/app_config.dart`:

```dart
// قم بإلغاء التعليق وضع IP الخاص بك
static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
```

#### ج. تأكد من:
- ✅ الجهاز والكمبيوتر على نفس الشبكة (WiFi)
- ✅ Backend يعمل على `0.0.0.0:8080` (وليس `localhost:8080`)
- ✅ Firewall لا يمنع الاتصال

---

## 🚀 التبديل إلى Production

عندما تريد نشر التطبيق:

```dart
// في lib/core/config/app_config.dart
static const bool _useProduction = true; // ✅ غيّر إلى true
```

سيستخدم التطبيق تلقائياً:
```
https://wasla-api.up.railway.app
```

---

## 🔍 معالجة الأخطاء الجديدة

### قبل الإصلاح ❌
```
كل خطأ = "No internet connection"
```

### بعد الإصلاح ✅

| الخطأ | الرسالة |
|-------|---------|
| **Backend مغلق** | "لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend" |
| **لا إنترنت** | "لا يوجد اتصال بالإنترنت. تحقق من اتصالك" |
| **بيانات خاطئة** | "بيانات الدخول غير صحيحة" |
| **Timeout** | "انتهت مهلة الاتصال" |
| **خطأ سيرفر** | "خطأ في الخادم. يرجى المحاولة لاحقاً" |
| **بريد مسجل** | "البريد الإلكتروني مسجل مسبقاً" |

---

## 🐛 استكشاف الأخطاء

### المشكلة: "لا يمكن الاتصال بالخادم"

**الحلول:**

1. **تأكد من تشغيل Backend:**
```bash
# في مجلد Backend
./mvnw spring-boot:run
# أو
java -jar target/wasla-backend.jar
```

2. **تحقق من المنفذ (Port):**
```
Backend يجب أن يعمل على: http://localhost:8080
```

3. **تحقق من الـ URL في التطبيق:**
```dart
// في main.dart أو أي ملف
import 'package:flutter_application_1/core/config/app_config.dart';

void main() {
  AppConfig.printConfig(); // ✅ سيطبع الإعدادات
  runApp(MyApp());
}
```

### المشكلة: "بيانات الدخول غير صحيحة"

**الحلول:**

1. **تأكد من وجود المستخدم في قاعدة البيانات**
2. **تحقق من صحة البريد وكلمة المرور**
3. **راجع logs الـ Backend**

### المشكلة: Physical Device لا يتصل

**الحلول:**

1. **تأكد من نفس الشبكة:**
```bash
# على الكمبيوتر
ping 192.168.1.100  # IP جهازك

# على الجهاز
# افتح متصفح واذهب إلى: http://192.168.1.100:8080
```

2. **تأكد من Backend يستمع على 0.0.0.0:**
```yaml
# في application.yml
server:
  address: 0.0.0.0  # ✅ وليس localhost
  port: 8080
```

3. **أوقف Firewall مؤقتاً للاختبار**

---

## 📊 Logging & Debugging

### تفعيل Logs

الـ Logs مفعّلة تلقائياً في Debug mode:

```
🌐 App Configuration:
   Platform: Android
   Base URL: http://10.0.2.2:8080
   API URL: http://10.0.2.2:8080/api/v1
   WebSocket: ws://10.0.2.2:8080/ws
   Production Mode: false

🌐 API: POST http://10.0.2.2:8080/api/v1/auth/login
📤 Body: {"email":"test@example.com","password":"password123"}
📥 Response Status: 200
📥 Response Body: {"success":true,"data":{...}}
```

### في حالة الخطأ:

```
❌ SocketException: Connection refused
🔴 Error: لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend
```

---

## 🧪 اختبار الاتصال

### 1. اختبار Backend يدوياً:

```bash
# في Terminal
curl http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### 2. اختبار من التطبيق:

```dart
// في أي screen
import 'package:flutter_application_1/core/config/app_config.dart';

@override
void initState() {
  super.initState();
  AppConfig.printConfig(); // طباعة الإعدادات
}
```

---

## 📝 ملاحظات مهمة

### ✅ ما تم إصلاحه:

1. **URL مركزي** - كل الـ URLs في مكان واحد
2. **كشف تلقائي للمنصة** - يختار URL الصحيح تلقائياً
3. **معالجة أخطاء دقيقة** - رسائل واضحة لكل نوع خطأ
4. **Logging محسّن** - سهل تتبع المشاكل
5. **Timeout handling** - لا انتظار لا نهائي

### ⚠️ تذكير:

- **Development**: `_useProduction = false`
- **Production**: `_useProduction = true`
- **Physical Device**: ضع IP الخاص بك في `_physicalDeviceUrl`

---

## 🎯 الخلاصة

| السيناريو | الإعداد |
|-----------|---------|
| **تطوير على Web** | لا شيء - يعمل تلقائياً |
| **تطوير على Android Emulator** | لا شيء - يعمل تلقائياً |
| **تطوير على iOS Simulator** | لا شيء - يعمل تلقائياً |
| **تطوير على Physical Device** | ضع IP في `_physicalDeviceUrl` |
| **Production** | غيّر `_useProduction = true` |

---

**تم الإصلاح بنجاح! ✅**

الآن يمكنك تسجيل الدخول بدون مشاكل على جميع المنصات.
