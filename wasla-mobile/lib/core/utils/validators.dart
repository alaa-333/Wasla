import '../constants/app_constants.dart';

/// دوال التحقق من صحة البيانات
class Validators {
  /// التحقق من البريد الإلكتروني
  static String? validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'البريد الإلكتروني مطلوب';
    }

    final emailRegex = RegExp(AppConstants.emailRegex);
    if (!emailRegex.hasMatch(value)) {
      return 'البريد الإلكتروني غير صحيح';
    }

    return null;
  }

  /// التحقق من رقم الهاتف
  static String? validatePhone(String? value) {
    if (value == null || value.isEmpty) {
      return 'رقم الهاتف مطلوب';
    }

    if (!value.startsWith('+')) {
      return 'يجب أن يبدأ الرقم بـ + ورمز الدولة (مثلاً +20)';
    }

    final phoneRegex = RegExp(AppConstants.phoneRegex);
    if (!phoneRegex.hasMatch(value)) {
      return 'رقم الهاتف غير صحيح';
    }

    return null;
  }

  /// التحقق من كلمة المرور
  static String? validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'كلمة المرور مطلوبة';
    }

    if (value.length < AppConstants.minPasswordLength) {
      return 'كلمة المرور يجب أن تكون ${AppConstants.minPasswordLength} أحرف على الأقل';
    }

    // التحقق من وجود حرف كبير
    if (!value.contains(RegExp(r'[A-Z]'))) {
      return 'كلمة المرور يجب أن تحتوي على حرف كبير';
    }

    // التحقق من وجود رقم
    if (!value.contains(RegExp(r'[0-9]'))) {
      return 'كلمة المرور يجب أن تحتوي على رقم';
    }

    return null;
  }

  /// التحقق من الاسم
  static String? validateName(String? value) {
    if (value == null || value.isEmpty) {
      return 'الاسم مطلوب';
    }

    if (value.length < 3) {
      return 'الاسم يجب أن يكون 3 أحرف على الأقل';
    }

    return null;
  }

  /// التحقق من التقييم
  static String? validateRating(int? value) {
    if (value == null) {
      return 'التقييم مطلوب';
    }

    if (value < AppConstants.minRating || value > AppConstants.maxRating) {
      return 'التقييم يجب أن يكون بين ${AppConstants.minRating} و ${AppConstants.maxRating}';
    }

    return null;
  }

  /// التحقق من السعر
  static String? validatePrice(String? value) {
    if (value == null || value.isEmpty) {
      return 'السعر مطلوب';
    }

    final price = double.tryParse(value);
    if (price == null || price <= 0) {
      return 'السعر يجب أن يكون رقماً موجباً';
    }

    return null;
  }

  /// التحقق من النص العام
  static String? validateRequired(String? value, String fieldName) {
    if (value == null || value.isEmpty) {
      return '$fieldName مطلوب';
    }
    return null;
  }
}
