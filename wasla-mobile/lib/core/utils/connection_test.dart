import 'dart:async';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../config/app_config.dart';

/// أداة اختبار الاتصال بالـ Backend
class ConnectionTest {
  /// اختبار الاتصال بالـ Backend
  static Future<ConnectionTestResult> testConnection() async {
    print('\n🔍 بدء اختبار الاتصال...\n');

    // طباعة الإعدادات
    AppConfig.printConfig();
    print('');

    final results = <String, bool>{};
    final errors = <String, String>{};

    // 1. اختبار localhost (للـ Web)
    await _testUrl(
      'http://localhost:8080/api/v1',
      'localhost',
      results,
      errors,
    );

    // 2. اختبار Android Emulator
    await _testUrl(
      'http://10.0.2.2:8080/api/v1',
      'Android Emulator',
      results,
      errors,
    );

    // 3. اختبار الـ URL المستخدم حالياً
    await _testUrl(AppConfig.apiBaseUrl, 'Current Config', results, errors);

    print('\n📊 نتائج الاختبار:\n');
    results.forEach((url, success) {
      final icon = success ? '✅' : '❌';
      print('$icon $url: ${success ? "متصل" : "غير متصل"}');
      if (!success && errors.containsKey(url)) {
        print('   السبب: ${errors[url]}');
      }
    });

    final anySuccess = results.values.any((v) => v);
    print(
      '\n${anySuccess ? "✅" : "❌"} النتيجة النهائية: ${anySuccess ? "يوجد اتصال" : "لا يوجد اتصال"}\n',
    );

    return ConnectionTestResult(
      success: anySuccess,
      results: results,
      errors: errors,
    );
  }

  static Future<void> _testUrl(
    String url,
    String name,
    Map<String, bool> results,
    Map<String, String> errors,
  ) async {
    try {
      print('🔄 اختبار $name: $url');

      final response = await http
          .get(Uri.parse('$url/auth/login'))
          .timeout(const Duration(seconds: 5));

      // أي استجابة (حتى 404 أو 405) تعني أن السيرفر يعمل
      final success = response.statusCode != null;
      results[name] = success;

      if (success) {
        print('   ✅ السيرفر يعمل (Status: ${response.statusCode})');
      }
    } on SocketException catch (e) {
      results[name] = false;
      if (e.osError?.errorCode == 111 ||
          e.osError?.errorCode == 61 ||
          e.osError?.errorCode == 10061) {
        errors[name] = 'السيرفر غير مشغّل';
        print('   ❌ السيرفر غير مشغّل');
      } else {
        errors[name] = 'خطأ في الاتصال: ${e.message}';
        print('   ❌ خطأ: ${e.message}');
      }
    } on TimeoutException {
      results[name] = false;
      errors[name] = 'انتهت المهلة';
      print('   ⏱️ انتهت المهلة');
    } catch (e) {
      results[name] = false;
      errors[name] = e.toString();
      print('   ❌ خطأ: $e');
    }
  }
}

class ConnectionTestResult {
  final bool success;
  final Map<String, bool> results;
  final Map<String, String> errors;

  ConnectionTestResult({
    required this.success,
    required this.results,
    required this.errors,
  });

  String getSuggestion() {
    if (success) {
      return 'الاتصال يعمل بشكل صحيح!';
    }

    final allFailed = results.values.every((v) => !v);
    if (allFailed) {
      return '''
❌ جميع محاولات الاتصال فشلت!

الحلول المقترحة:
1. تأكد من تشغيل Backend على المنفذ 8080
2. تحقق من أن Backend يعمل على 0.0.0.0 وليس localhost فقط
3. تأكد من عدم وجود Firewall يمنع الاتصال
4. جرب الأمر: curl http://localhost:8080/api/v1

لتشغيل Backend:
cd backend-folder
./mvnw spring-boot:run
''';
    }

    return 'بعض الاتصالات تعمل. راجع النتائج أعلاه.';
  }
}
