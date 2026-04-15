import 'dart:io';
import 'package:dio/dio.dart';

/// Custom API Exception with detailed error handling
class ApiException implements Exception {
  final String message;
  final int? statusCode;
  final Map<String, dynamic>? fieldErrors;
  final ApiErrorType errorType;

  ApiException({
    required this.message,
    this.statusCode,
    this.fieldErrors,
    this.errorType = ApiErrorType.unknown,
  });

  factory ApiException.fromDioError(DioException error) {
    String message = 'حدث خطأ غير متوقع';
    int? statusCode;
    Map<String, dynamic>? fieldErrors;
    ApiErrorType errorType = ApiErrorType.unknown;

    // Handle response errors (server responded with error)
    if (error.response != null) {
      statusCode = error.response!.statusCode;
      final data = error.response!.data;

      if (data is Map<String, dynamic>) {
        message = data['message'] ?? message;

        // Handle field errors (validation errors)
        if (data['fieldErrors'] != null) {
          fieldErrors = {};
          for (var fieldError in data['fieldErrors']) {
            fieldErrors[fieldError['field']] = fieldError['message'];
          }
        }
      }

      // Custom messages based on status code
      switch (statusCode) {
        case 400:
          errorType = ApiErrorType.badRequest;
          message = fieldErrors != null
              ? 'يرجى التحقق من البيانات المدخلة'
              : (data is Map
                    ? (data['message'] ?? 'طلب غير صالح')
                    : 'طلب غير صالح');
          break;
        case 401:
          errorType = ApiErrorType.unauthorized;
          message = 'بيانات الدخول غير صحيحة';
          break;
        case 403:
          errorType = ApiErrorType.forbidden;
          message = 'ليس لديك صلاحية للقيام بهذا الإجراء';
          break;
        case 404:
          errorType = ApiErrorType.notFound;
          message = 'المورد المطلوب غير موجود';
          break;
        case 409:
          errorType = ApiErrorType.conflict;
          message = data is Map
              ? (data['message'] ?? 'تعارض في البيانات')
              : 'تعارض في البيانات';
          break;
        case 500:
        case 502:
        case 503:
          errorType = ApiErrorType.serverError;
          message = 'خطأ في الخادم. يرجى المحاولة لاحقاً';
          break;
      }
    } else {
      // Handle network/connection errors (no response from server)
      switch (error.type) {
        case DioExceptionType.connectionTimeout:
          errorType = ApiErrorType.timeout;
          message = 'انتهت مهلة الاتصال. يرجى المحاولة مجدداً';
          break;
        case DioExceptionType.sendTimeout:
          errorType = ApiErrorType.timeout;
          message = 'انتهت مهلة إرسال البيانات. يرجى المحاولة مجدداً';
          break;
        case DioExceptionType.receiveTimeout:
          errorType = ApiErrorType.timeout;
          message = 'انتهت مهلة استقبال البيانات. يرجى المحاولة مجدداً';
          break;
        case DioExceptionType.connectionError:
          errorType = ApiErrorType.noInternet;
          // Check if it's actually a connection error or server not running
          if (error.error is SocketException) {
            final socketError = error.error as SocketException;
            if (socketError.osError?.errorCode ==
                    111 || // Connection refused (Linux)
                socketError.osError?.errorCode ==
                    61 || // Connection refused (macOS)
                socketError.osError?.errorCode == 10061) {
              // Connection refused (Windows)
              message = 'لا يمكن الاتصال بالخادم. تأكد من تشغيل Backend';
            } else {
              message = 'لا يوجد اتصال بالإنترنت. تحقق من اتصالك';
            }
          } else {
            message = 'فشل الاتصال بالخادم';
          }
          break;
        case DioExceptionType.badResponse:
          errorType = ApiErrorType.serverError;
          message = 'استجابة غير صالحة من الخادم';
          break;
        case DioExceptionType.cancel:
          errorType = ApiErrorType.cancelled;
          message = 'تم إلغاء الطلب';
          break;
        default:
          errorType = ApiErrorType.unknown;
          message = 'حدث خطأ في الاتصال';
      }
    }

    return ApiException(
      message: message,
      statusCode: statusCode,
      fieldErrors: fieldErrors,
      errorType: errorType,
    );
  }

  /// Get user-friendly error message
  String get userMessage => message;

  /// Check if error is due to no internet
  bool get isNoInternet => errorType == ApiErrorType.noInternet;

  /// Check if error is due to server being down
  bool get isServerDown =>
      errorType == ApiErrorType.serverError ||
      (errorType == ApiErrorType.noInternet && message.contains('Backend'));

  /// Check if error is authentication related
  bool get isAuthError =>
      errorType == ApiErrorType.unauthorized ||
      errorType == ApiErrorType.forbidden;

  @override
  String toString() => message;
}

/// Types of API errors for better error handling
enum ApiErrorType {
  noInternet,
  timeout,
  serverError,
  unauthorized,
  forbidden,
  notFound,
  badRequest,
  conflict,
  cancelled,
  unknown,
}
