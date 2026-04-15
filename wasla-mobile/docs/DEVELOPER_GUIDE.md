# 👨‍💻 دليل المطور - Developer Guide

دليل شامل للمطورين الذين سيعملون على مشروع وصلة.

---

## 📚 جدول المحتويات

1. [البداية السريعة](#البداية-السريعة)
2. [هيكل المشروع](#هيكل-المشروع)
3. [معايير الكود](#معايير-الكود)
4. [إضافة ميزة جديدة](#إضافة-ميزة-جديدة)
5. [التعامل مع API](#التعامل-مع-api)
6. [إدارة الحالة](#إدارة-الحالة)
7. [معالجة الأخطاء](#معالجة-الأخطاء)
8. [الاختبارات](#الاختبارات)
9. [نصائح وأفضل الممارسات](#نصائح-وأفضل-الممارسات)

---

## 🚀 البداية السريعة

### المتطلبات
```bash
Flutter SDK: 3.11.0+
Dart SDK: 3.0.0+
Android Studio / VS Code
Git
```

### التثبيت
```bash
# 1. Clone المشروع
git clone <repository-url>
cd flutter_application_1

# 2. تثبيت المكتبات
flutter pub get

# 3. تشغيل التطبيق
flutter run
```

### تشغيل Backend
```bash
# تأكد من تشغيل Spring Boot backend
# على http://localhost:8080
```

---

## 📁 هيكل المشروع

```
lib/
├── core/                    # الأساسيات المشتركة
│   ├── constants/          # الثوابت (API URLs, Keys)
│   ├── network/            # Dio Client, Interceptors
│   └── theme/              # الألوان والثيمات
│
├── data/                   # طبقة البيانات
│   ├── models/            # نماذج البيانات (DTOs)
│   └── services/          # خدمات API
│
└── presentation/          # طبقة العرض
    ├── screens/          # الشاشات
    └── widgets/          # Widgets قابلة لإعادة الاستخدام
```

### قواعد التنظيم

1. **core/** - كل ما هو مشترك ومستخدم في كل المشروع
2. **data/** - كل ما يتعلق بالبيانات والـ API
3. **presentation/** - كل ما يتعلق بالواجهة

---

## 📝 معايير الكود

### تسمية الملفات
```dart
// ✅ صحيح
user_model.dart
auth_service.dart
login_screen.dart

// ❌ خطأ
UserModel.dart
authService.dart
LoginScreen.dart
```

### تسمية الكلاسات
```dart
// ✅ صحيح
class UserModel { }
class AuthService { }
class LoginScreen extends StatefulWidget { }

// ❌ خطأ
class user_model { }
class authService { }
```

### تسمية المتغيرات
```dart
// ✅ صحيح
final String userName;
bool isLoading = false;
const int maxRetries = 3;

// ❌ خطأ
final String UserName;
bool IsLoading = false;
const int MAX_RETRIES = 3;
```

### تسمية الثوابت
```dart
// ✅ صحيح
class ApiConstants {
  static const String baseUrl = 'http://localhost:8080';
  static const String login = '/auth/login';
}

// ❌ خطأ
class ApiConstants {
  static const String BASE_URL = 'http://localhost:8080';
  static const String LOGIN = '/auth/login';
}
```

---

## ➕ إضافة ميزة جديدة

### 1. إضافة Model جديد

```dart
// lib/data/models/notification_model.dart

class NotificationModel {
  final String id;
  final String title;
  final String body;
  final DateTime createdAt;

  NotificationModel({
    required this.id,
    required this.title,
    required this.body,
    required this.createdAt,
  });

  factory NotificationModel.fromJson(Map<String, dynamic> json) {
    return NotificationModel(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      body: json['body'] ?? '',
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'body': body,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
```

### 2. إضافة Service جديد

```dart
// lib/data/services/notification_service.dart

import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/notification_model.dart';
import '../models/api_response_model.dart';

class NotificationService {
  final DioClient _dioClient = DioClient.instance;

  Future<List<NotificationModel>> getNotifications() async {
    try {
      final response = await _dioClient.get('/notifications');

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => (data as List)
            .map((item) => NotificationModel.fromJson(item))
            .toList(),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
```

### 3. إضافة شاشة جديدة

```dart
// lib/presentation/screens/notifications/notifications_screen.dart

import 'package:flutter/material.dart';
import '../../../data/services/notification_service.dart';
import '../../../data/models/notification_model.dart';
import '../../widgets/loading_overlay.dart';
import '../../widgets/custom_snackbar.dart';
import '../../widgets/empty_state.dart';

class NotificationsScreen extends StatefulWidget {
  const NotificationsScreen({super.key});

  @override
  State<NotificationsScreen> createState() => _NotificationsScreenState();
}

class _NotificationsScreenState extends State<NotificationsScreen> {
  final NotificationService _notificationService = NotificationService();
  List<NotificationModel> _notifications = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadNotifications();
  }

  Future<void> _loadNotifications() async {
    setState(() => _isLoading = true);

    try {
      final notifications = await _notificationService.getNotifications();
      if (mounted) {
        setState(() => _notifications = notifications);
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('الإشعارات'),
      ),
      body: LoadingOverlay(
        isLoading: _isLoading,
        child: _notifications.isEmpty
            ? const EmptyState(
                icon: Icons.notifications_none,
                title: 'لا توجد إشعارات',
                subtitle: 'ستظهر هنا الإشعارات الجديدة',
              )
            : ListView.builder(
                itemCount: _notifications.length,
                itemBuilder: (context, index) {
                  final notification = _notifications[index];
                  return ListTile(
                    leading: const Icon(Icons.notifications),
                    title: Text(notification.title),
                    subtitle: Text(notification.body),
                  );
                },
              ),
      ),
    );
  }
}
```

### 4. إضافة المسار في main.dart

```dart
routes: {
  // ... existing routes
  '/notifications': (context) => const NotificationsScreen(),
}
```

---

## 🌐 التعامل مع API

### استدعاء API بسيط

```dart
// في الشاشة
Future<void> _loadData() async {
  setState(() => _isLoading = true);

  try {
    final data = await _service.getData();
    if (mounted) {
      setState(() => _data = data);
    }
  } catch (e) {
    if (mounted) {
      CustomSnackbar.showError(context, e.toString());
    }
  } finally {
    if (mounted) {
      setState(() => _isLoading = false);
    }
  }
}
```

### استدعاء API مع معاملات

```dart
final jobs = await _jobService.getNearbyJobs(
  lat: 30.0444,
  lng: 31.2357,
  radiusKm: 15,
);
```

### استدعاء API مع Pagination

```dart
final response = await _jobService.getMyJobs(
  page: _currentPage,
  size: 20,
);

setState(() {
  _jobs.addAll(response.content);
  _hasMore = !response.last;
  _currentPage++;
});
```

---

## 🔄 إدارة الحالة

### استخدام setState

```dart
// ✅ صحيح
setState(() {
  _isLoading = true;
  _data = newData;
});

// ❌ خطأ - لا تستدعي setState بدون تغيير
setState(() {});
_isLoading = true;
```

### التحقق من mounted

```dart
// ✅ صحيح - دائماً تحقق من mounted قبل setState
if (mounted) {
  setState(() => _isLoading = false);
}

// ❌ خطأ - قد يسبب خطأ إذا تم dispose
setState(() => _isLoading = false);
```

---

## ⚠️ معالجة الأخطاء

### معالجة أخطاء API

```dart
try {
  final result = await _service.method();
  // Success
} on ApiException catch (e) {
  // معالجة أخطاء API المخصصة
  CustomSnackbar.showError(context, e.message);
  
  // عرض أخطاء الحقول
  if (e.fieldErrors != null) {
    e.fieldErrors!.forEach((field, message) {
      print('$field: $message');
    });
  }
} catch (e) {
  // معالجة أخطاء عامة
  CustomSnackbar.showError(context, 'حدث خطأ غير متوقع');
}
```

### معالجة أخطاء الشبكة

```dart
try {
  final result = await _service.method();
} on DioException catch (e) {
  if (e.type == DioExceptionType.connectionTimeout) {
    CustomSnackbar.showError(context, 'انتهت مهلة الاتصال');
  } else if (e.type == DioExceptionType.connectionError) {
    CustomSnackbar.showError(context, 'لا يوجد اتصال بالإنترنت');
  }
}
```

---

## 🧪 الاختبارات

### Unit Test مثال

```dart
// test/services/auth_service_test.dart

import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_application_1/data/services/auth_service.dart';

void main() {
  group('AuthService', () {
    late AuthService authService;

    setUp(() {
      authService = AuthService();
    });

    test('login should return AuthResponseModel on success', () async {
      // Arrange
      final email = 'test@example.com';
      final password = 'password123';

      // Act
      final result = await authService.login(
        email: email,
        password: password,
      );

      // Assert
      expect(result, isA<AuthResponseModel>());
      expect(result.user.email, email);
    });
  });
}
```

### Widget Test مثال

```dart
// test/widgets/status_badge_test.dart

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_application_1/presentation/widgets/status_badge.dart';

void main() {
  testWidgets('StatusBadge displays correct text', (tester) async {
    // Arrange
    await tester.pumpWidget(
      const MaterialApp(
        home: Scaffold(
          body: StatusBadge(status: 'OPEN'),
        ),
      ),
    );

    // Assert
    expect(find.text('مفتوحة'), findsOneWidget);
  });
}
```

---

## 💡 نصائح وأفضل الممارسات

### 1. استخدم const حيثما أمكن

```dart
// ✅ صحيح
const Text('Hello');
const SizedBox(height: 16);

// ❌ خطأ
Text('Hello');
SizedBox(height: 16);
```

### 2. استخدم final للمتغيرات غير المتغيرة

```dart
// ✅ صحيح
final String name = 'Ahmed';
final int age = 25;

// ❌ خطأ
String name = 'Ahmed';
int age = 25;
```

### 3. استخدم Widgets منفصلة

```dart
// ✅ صحيح
class _JobCard extends StatelessWidget {
  final JobModel job;
  const _JobCard({required this.job});
  
  @override
  Widget build(BuildContext context) {
    return Card(/* ... */);
  }
}

// ❌ خطأ - كل شيء في build method
@override
Widget build(BuildContext context) {
  return Column(
    children: [
      Card(/* ... */),
      Card(/* ... */),
      Card(/* ... */),
    ],
  );
}
```

### 4. استخدم Async/Await بشكل صحيح

```dart
// ✅ صحيح
Future<void> _loadData() async {
  try {
    final data = await _service.getData();
    setState(() => _data = data);
  } catch (e) {
    // handle error
  }
}

// ❌ خطأ
void _loadData() {
  _service.getData().then((data) {
    setState(() => _data = data);
  }).catchError((e) {
    // handle error
  });
}
```

### 5. نظّف الموارد في dispose

```dart
@override
void dispose() {
  _controller.dispose();
  _subscription.cancel();
  super.dispose();
}
```

### 6. استخدم SafeArea

```dart
// ✅ صحيح
Scaffold(
  body: SafeArea(
    child: /* ... */,
  ),
)
```

### 7. استخدم MediaQuery بحذر

```dart
// ✅ صحيح - احفظ في متغير
final screenWidth = MediaQuery.of(context).size.width;

// ❌ خطأ - استدعاء متكرر
Container(
  width: MediaQuery.of(context).size.width * 0.5,
  height: MediaQuery.of(context).size.height * 0.3,
)
```

### 8. استخدم Keys عند الحاجة

```dart
ListView.builder(
  itemCount: items.length,
  itemBuilder: (context, index) {
    return ListTile(
      key: ValueKey(items[index].id),
      title: Text(items[index].name),
    );
  },
)
```

---

## 🔧 أدوات مفيدة

### VS Code Extensions
- Flutter
- Dart
- Flutter Widget Snippets
- Error Lens
- GitLens

### Android Studio Plugins
- Flutter
- Dart
- Flutter Enhancement Suite

### أوامر مفيدة

```bash
# تحليل الكود
flutter analyze

# تنسيق الكود
flutter format .

# تشغيل الاختبارات
flutter test

# بناء APK
flutter build apk

# بناء iOS
flutter build ios

# تنظيف المشروع
flutter clean
```

---

## 📞 الدعم

إذا واجهت أي مشكلة:
1. راجع التوثيق
2. ابحث في Issues
3. اسأل الفريق
4. أنشئ Issue جديد

---

**آخر تحديث:** 2026  
**الإصدار:** 1.0.0
