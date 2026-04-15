import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../config/app_config.dart';
import 'api_response.dart';
import 'auth_storage.dart';

/// API Client للتعامل مع Wasla Backend
/// يدعم JWT Authentication مع Token Refresh تلقائي
class ApiClient {
  // الرابط الأساسي من AppConfig
  static String get baseUrl => AppConfig.apiBaseUrl;

  /// إرسال طلب GET
  static Future<ApiResponse> get(
    String endpoint, {
    Map<String, String>? queryParams,
    bool requiresAuth = true,
  }) async {
    try {
      final uri = Uri.parse(
        '$baseUrl$endpoint',
      ).replace(queryParameters: queryParams);

      print('🌐 GET Request: $uri');

      final headers = await _buildHeaders(requiresAuth);
      final response = await http
          .get(uri, headers: headers)
          .timeout(Duration(seconds: AppConfig.connectionTimeout));

      print('📥 Response Status: ${response.statusCode}');
      return _handleResponse(response);
    } on SocketException catch (e) {
      print('❌ SocketException: ${e.message}');
      if (e.osError?.errorCode == 111 ||
          e.osError?.errorCode == 61 ||
          e.osError?.errorCode == 10061) {
        return ApiResponse.error(
          'لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend',
        );
      }
      return ApiResponse.error('لا يوجد اتصال بالإنترنت');
    } on TimeoutException {
      print('⏱️ Timeout');
      return ApiResponse.error('انتهت مهلة الاتصال');
    } catch (e) {
      print('❌ Error: $e');
      return ApiResponse.error('خطأ في الاتصال: $e');
    }
  }

  /// إرسال طلب POST
  static Future<ApiResponse> post(
    String endpoint, {
    Map<String, dynamic>? body,
    bool requiresAuth = true,
  }) async {
    try {
      final uri = Uri.parse('$baseUrl$endpoint');
      print('🌐 POST Request: $uri');
      if (body != null) print('📤 Body: ${jsonEncode(body)}');

      final headers = await _buildHeaders(requiresAuth);

      final response = await http
          .post(
            uri,
            headers: headers,
            body: body != null ? jsonEncode(body) : null,
          )
          .timeout(Duration(seconds: AppConfig.connectionTimeout));

      print('📥 Response Status: ${response.statusCode}');
      print('📥 Response Body: ${response.body}');
      return _handleResponse(response);
    } on SocketException catch (e) {
      print('❌ SocketException: ${e.message}');
      if (e.osError?.errorCode == 111 ||
          e.osError?.errorCode == 61 ||
          e.osError?.errorCode == 10061) {
        return ApiResponse.error(
          'لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend',
        );
      }
      return ApiResponse.error('لا يوجد اتصال بالإنترنت');
    } on TimeoutException {
      print('⏱️ Timeout');
      return ApiResponse.error('انتهت مهلة الاتصال');
    } catch (e) {
      print('❌ Error: $e');
      return ApiResponse.error('خطأ في الاتصال: $e');
    }
  }

  /// إرسال طلب PUT
  static Future<ApiResponse> put(
    String endpoint, {
    Map<String, dynamic>? body,
    bool requiresAuth = true,
  }) async {
    try {
      final uri = Uri.parse('$baseUrl$endpoint');
      print('🌐 PUT Request: $uri');

      final headers = await _buildHeaders(requiresAuth);

      final response = await http
          .put(
            uri,
            headers: headers,
            body: body != null ? jsonEncode(body) : null,
          )
          .timeout(Duration(seconds: AppConfig.connectionTimeout));

      print('📥 Response Status: ${response.statusCode}');
      return _handleResponse(response);
    } on SocketException catch (e) {
      print('❌ SocketException: ${e.message}');
      return ApiResponse.error('لا يوجد اتصال بالإنترنت');
    } on TimeoutException {
      print('⏱️ Timeout');
      return ApiResponse.error('انتهت مهلة الاتصال');
    } catch (e) {
      print('❌ Error: $e');
      return ApiResponse.error('خطأ في الاتصال: $e');
    }
  }

  /// إرسال طلب PATCH
  static Future<ApiResponse> patch(
    String endpoint, {
    Map<String, dynamic>? body,
    bool requiresAuth = true,
  }) async {
    try {
      final uri = Uri.parse('$baseUrl$endpoint');
      print('🌐 PATCH Request: $uri');

      final headers = await _buildHeaders(requiresAuth);

      final response = await http
          .patch(
            uri,
            headers: headers,
            body: body != null ? jsonEncode(body) : null,
          )
          .timeout(Duration(seconds: AppConfig.connectionTimeout));

      print('📥 Response Status: ${response.statusCode}');
      return _handleResponse(response);
    } on SocketException catch (e) {
      print('❌ SocketException: ${e.message}');
      return ApiResponse.error('لا يوجد اتصال بالإنترنت');
    } on TimeoutException {
      print('⏱️ Timeout');
      return ApiResponse.error('انتهت مهلة الاتصال');
    } catch (e) {
      print('❌ Error: $e');
      return ApiResponse.error('خطأ في الاتصال: $e');
    }
  }

  /// إرسال طلب DELETE
  static Future<ApiResponse> delete(
    String endpoint, {
    bool requiresAuth = true,
  }) async {
    try {
      final uri = Uri.parse('$baseUrl$endpoint');
      print('🌐 DELETE Request: $uri');

      final headers = await _buildHeaders(requiresAuth);

      final response = await http
          .delete(uri, headers: headers)
          .timeout(Duration(seconds: AppConfig.connectionTimeout));

      print('📥 Response Status: ${response.statusCode}');
      return _handleResponse(response);
    } on SocketException catch (e) {
      print('❌ SocketException: ${e.message}');
      return ApiResponse.error('لا يوجد اتصال بالإنترنت');
    } on TimeoutException {
      print('⏱️ Timeout');
      return ApiResponse.error('انتهت مهلة الاتصال');
    } catch (e) {
      print('❌ Error: $e');
      return ApiResponse.error('خطأ في الاتصال: $e');
    }
  }

  /// بناء الـ Headers مع JWT Token
  static Future<Map<String, String>> _buildHeaders(bool requiresAuth) async {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };

    if (requiresAuth) {
      final token = await AuthStorage.getAccessToken();
      if (token != null) {
        headers['Authorization'] = 'Bearer $token';
      }
    }

    return headers;
  }

  /// معالجة الاستجابة من السيرفر
  static ApiResponse _handleResponse(http.Response response) {
    final statusCode = response.statusCode;

    // محاولة فك تشفير الـ JSON
    dynamic jsonData;
    try {
      jsonData = jsonDecode(response.body);
    } catch (e) {
      jsonData = {'message': response.body};
    }

    // حالات النجاح (200-299)
    if (statusCode >= 200 && statusCode < 300) {
      return ApiResponse.success(jsonData);
    }

    // حالة 401 - بيانات دخول خاطئة أو Token منتهي
    if (statusCode == 401) {
      return ApiResponse(
        success: false,
        statusCode: 401,
        message: jsonData['message'] ?? 'بيانات الدخول غير صحيحة',
        data: null,
      );
    }

    // حالة 403 - ممنوع (صلاحيات غير كافية)
    if (statusCode == 403) {
      return ApiResponse.error(
        jsonData['message'] ?? 'ليس لديك صلاحية للقيام بهذا الإجراء',
      );
    }

    // حالة 404 - غير موجود
    if (statusCode == 404) {
      return ApiResponse.error(
        jsonData['message'] ?? 'المورد المطلوب غير موجود',
      );
    }

    // حالة 409 - تعارض (مثل: البريد مسجل مسبقاً)
    if (statusCode == 409) {
      return ApiResponse.error(
        jsonData['message'] ?? 'البيانات المدخلة متعارضة',
      );
    }

    // حالة 400 - خطأ في البيانات
    if (statusCode == 400) {
      // التحقق من وجود fieldErrors
      if (jsonData is Map && jsonData['fieldErrors'] != null) {
        final errors = (jsonData['fieldErrors'] as List)
            .map((e) => e['message'] as String)
            .join('\n');
        return ApiResponse.error(errors);
      }
      return ApiResponse.error(
        jsonData is Map
            ? (jsonData['message'] ?? 'خطأ في البيانات المرسلة')
            : 'خطأ في البيانات المرسلة',
      );
    }

    // حالة 500 - خطأ في السيرفر
    if (statusCode >= 500) {
      return ApiResponse.error('خطأ في الخادم، حاول مرة أخرى لاحقاً');
    }

    // حالة غير معروفة
    return ApiResponse.error(
      jsonData is Map
          ? (jsonData['message'] ?? 'حدث خطأ غير متوقع')
          : 'حدث خطأ غير متوقع',
    );
  }

  /// تجديد الـ Access Token باستخدام Refresh Token
  static Future<bool> refreshToken() async {
    try {
      final refreshToken = await AuthStorage.getRefreshToken();
      if (refreshToken == null) return false;

      final response = await post(
        '/auth/refresh',
        body: {'refreshToken': refreshToken},
        requiresAuth: false,
      );

      if (response.success && response.data != null) {
        final data = response.data['data'];
        await AuthStorage.saveTokens(
          accessToken: data['accessToken'],
          refreshToken: data['refreshToken'],
        );
        return true;
      }

      return false;
    } catch (e) {
      return false;
    }
  }
}
