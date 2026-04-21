/// ثوابت التطبيق
class AppConstants {
  // معلومات التطبيق
  static const String appName = 'وصلة - Wasla';
  static const String appVersion = '1.0.0';

  // حالات الوظيفة (Job Status)
  static const String jobStatusOpen = 'OPEN';
  static const String jobStatusBidding = 'BIDDING';
  static const String jobStatusConfirmed = 'CONFIRMED';
  static const String jobStatusInProgress = 'IN_PROGRESS';
  static const String jobStatusCompleted = 'COMPLETED';
  static const String jobStatusExpired = 'EXPIRED';

  // حالات العرض (Bid Status)
  static const String bidStatusPending = 'PENDING';
  static const String bidStatusAccepted = 'ACCEPTED';
  static const String bidStatusWithdrawn = 'WITHDRAWN';

  // أدوار المستخدمين
  static const String roleClient = 'CLIENT';
  static const String roleDriver = 'DRIVER';

  // أنواع المركبات
  static const String vehiclePickupOneTon = 'PICKUP_ONE_TON';
  static const String vehiclePickupTwoTon = 'PICKUP_TWO_TON';
  static const String vehicleTruckSmall = 'TRUCK_SMALL';
  static const String vehicleTruckMedium = 'TRUCK_MEDIUM';
  static const String vehicleTruckLarge = 'TRUCK_LARGE';

  // الحدود
  static const int maxImageSize = 5 * 1024 * 1024; // 5 MB
  static const int maxImagesCount = 5;
  static const int minPasswordLength = 8;
  static const int jobExpiryMinutes = 30;

  // التقييم
  static const int minRating = 1;
  static const int maxRating = 5;

  // الخريطة
  static const double defaultZoom = 13.0;
  static const double trackingZoom = 15.0;
  static const int locationUpdateInterval = 5; // ثواني

  // الرسائل
  static const String msgNetworkError = 'خطأ في الاتصال بالإنترنت';
  static const String msgServerError = 'خطأ في السيرفر، حاول مرة أخرى';
  static const String msgSessionExpired =
      'انتهت صلاحية الجلسة، سجل دخول مرة أخرى';
  static const String msgSuccess = 'تمت العملية بنجاح';

  // صيغة رقم الهاتف
  static const String phoneRegex = r'^\+[1-9]\d{1,14}$';
  static const String phoneHint = '+201012345678';

  // صيغة البريد الإلكتروني
  static const String emailRegex = r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$';

  // WebSocket
  static const String wsEndpoint = '/ws';
  static const String wsDriverLocationTopic = '/app/driver.location';
  static const String wsJobLocationTopic = '/topic/job/{jobId}/location';
}
