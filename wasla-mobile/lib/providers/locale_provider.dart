import 'package:flutter/material.dart';

class LocaleProvider extends ChangeNotifier {
  // اللغة الافتراضية للتطبيق هي العربي
  Locale _locale = const Locale('ar');

  Locale get locale => _locale;

  // دالة لتغيير اللغة
  void setLocale(Locale locale) {
    // لو اللغة الجديدة هي هي القديمة، مفيش داعي نحدث الشاشة
    if (_locale == locale) return;

    _locale = locale;
    notifyListeners(); // تنبيه التطبيق بالكامل للتحديث
  }

  // دالة سريعة لو حبيت تعمل زرار "تبديل اللغة" Toggle
  void toggleLocale() {
    if (_locale.languageCode == 'ar') {
      setLocale(const Locale('en'));
    } else {
      setLocale(const Locale('ar'));
    }
  }
}
