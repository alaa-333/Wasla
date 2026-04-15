/// نموذج موحد للاستجابة من الـ API
/// يتبع الصيغة الموحدة من الدليل (نقطة 7)
class ApiResponse {
  final bool success;
  final int statusCode;
  final String message;
  final dynamic data;

  ApiResponse({
    required this.success,
    required this.statusCode,
    required this.message,
    this.data,
  });

  /// استجابة ناجحة
  factory ApiResponse.success(dynamic jsonData) {
    return ApiResponse(
      success: true,
      statusCode: 200,
      message: jsonData['message'] ?? 'Success',
      data: jsonData['data'],
    );
  }

  /// استجابة خطأ
  factory ApiResponse.error(String message, {int statusCode = 400}) {
    return ApiResponse(
      success: false,
      statusCode: statusCode,
      message: message,
      data: null,
    );
  }

  @override
  String toString() {
    return 'ApiResponse(success: $success, statusCode: $statusCode, message: $message)';
  }
}
