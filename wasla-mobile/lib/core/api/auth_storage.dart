import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// إدارة تخزين الـ JWT Tokens بشكل آمن
/// يستخدم flutter_secure_storage حسب توصيات الدليل (نقطة 10)
class AuthStorage {
  static const _storage = FlutterSecureStorage();

  // مفاتيح التخزين
  static const _accessTokenKey = 'access_token';
  static const _refreshTokenKey = 'refresh_token';
  static const _userIdKey = 'user_id';
  static const _userRoleKey = 'user_role';
  static const _userNameKey = 'user_name';
  static const _userEmailKey = 'user_email';
  static const _userPhoneKey = 'user_phone';

  /// حفظ الـ Tokens بعد تسجيل الدخول أو التسجيل
  static Future<void> saveTokens({
    required String accessToken,
    required String refreshToken,
  }) async {
    await _storage.write(key: _accessTokenKey, value: accessToken);
    await _storage.write(key: _refreshTokenKey, value: refreshToken);
  }

  /// حفظ بيانات المستخدم
  static Future<void> saveUserData({
    required String userId,
    required String role,
    required String name,
    required String email,
    required String phone,
  }) async {
    await _storage.write(key: _userIdKey, value: userId);
    await _storage.write(key: _userRoleKey, value: role);
    await _storage.write(key: _userNameKey, value: name);
    await _storage.write(key: _userEmailKey, value: email);
    await _storage.write(key: _userPhoneKey, value: phone);
  }

  /// الحصول على Access Token
  static Future<String?> getAccessToken() async {
    return await _storage.read(key: _accessTokenKey);
  }

  /// الحصول على Refresh Token
  static Future<String?> getRefreshToken() async {
    return await _storage.read(key: _refreshTokenKey);
  }

  /// الحصول على دور المستخدم (CLIENT أو DRIVER)
  static Future<String?> getUserRole() async {
    return await _storage.read(key: _userRoleKey);
  }

  /// الحصول على اسم المستخدم
  static Future<String?> getUserName() async {
    return await _storage.read(key: _userNameKey);
  }

  /// الحصول على ID المستخدم
  static Future<String?> getUserId() async {
    return await _storage.read(key: _userIdKey);
  }

  /// الحصول على البريد الإلكتروني
  static Future<String?> getUserEmail() async {
    return await _storage.read(key: _userEmailKey);
  }

  /// الحصول على رقم الهاتف
  static Future<String?> getUserPhone() async {
    return await _storage.read(key: _userPhoneKey);
  }

  /// التحقق من وجود جلسة نشطة
  static Future<bool> isLoggedIn() async {
    final token = await getAccessToken();
    return token != null && token.isNotEmpty;
  }

  /// مسح جميع البيانات (تسجيل الخروج)
  static Future<void> clearAll() async {
    await _storage.deleteAll();
  }
}
