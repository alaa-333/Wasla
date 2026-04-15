import 'package:flutter/material.dart';

/// خدمة الإشعارات - جاهزة للتكامل مع Firebase FCM
/// حالياً تعرض إشعارات محلية فقط
class NotificationService {
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  /// عرض إشعار محلي
  static void showLocalNotification(
    BuildContext context, {
    required String title,
    required String message,
    Color? backgroundColor,
    IconData? icon,
  }) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            if (icon != null) ...[
              Icon(icon, color: Colors.white),
              const SizedBox(width: 10),
            ],
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                    ),
                  ),
                  Text(message),
                ],
              ),
            ),
          ],
        ),
        backgroundColor: backgroundColor ?? Colors.blue,
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 4),
        action: SnackBarAction(
          label: 'إغلاق',
          textColor: Colors.white,
          onPressed: () {},
        ),
      ),
    );
  }

  /// إشعار نجاح
  static void showSuccess(BuildContext context, String message) {
    showLocalNotification(
      context,
      title: 'نجح',
      message: message,
      backgroundColor: Colors.green,
      icon: Icons.check_circle,
    );
  }

  /// إشعار خطأ
  static void showError(BuildContext context, String message) {
    showLocalNotification(
      context,
      title: 'خطأ',
      message: message,
      backgroundColor: Colors.red,
      icon: Icons.error,
    );
  }

  /// إشعار تحذير
  static void showWarning(BuildContext context, String message) {
    showLocalNotification(
      context,
      title: 'تنبيه',
      message: message,
      backgroundColor: Colors.orange,
      icon: Icons.warning,
    );
  }

  /// إشعار معلومات
  static void showInfo(BuildContext context, String message) {
    showLocalNotification(
      context,
      title: 'معلومة',
      message: message,
      backgroundColor: Colors.blue,
      icon: Icons.info,
    );
  }

  // ملاحظة: لإضافة Firebase Cloud Messaging:
  // 1. أضف firebase_messaging package
  // 2. قم بتهيئة Firebase
  // 3. اطلب الأذونات
  // 4. احصل على FCM Token
  // 5. أرسل Token للسيرفر

  // Future<void> initializeFCM() async {
  //   // Implementation here
  // }
}
