import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/auth_response_model.dart';
import '../models/api_response_model.dart';

class AuthService {
  final DioClient _dioClient = DioClient.instance;
  final FlutterSecureStorage _storage = const FlutterSecureStorage();

  /// Login
  Future<AuthResponseModel> login({
    required String email,
    required String password,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.login,
        data: {'email': email, 'password': password},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => AuthResponseModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        await _saveTokens(apiResponse.data!);
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Register Client
  Future<AuthResponseModel> registerClient({
    required String fullName,
    required String email,
    required String phone,
    required String password,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.registerClient,
        data: {
          'fullName': fullName,
          'email': email,
          'phone': phone,
          'password': password,
        },
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => AuthResponseModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        await _saveTokens(apiResponse.data!);
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Register Driver
  Future<AuthResponseModel> registerDriver({
    required String fullName,
    required String email,
    required String phone,
    required String password,
    required String vehicleType,
    required String licensePlate,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.registerDriver,
        data: {
          'fullName': fullName,
          'email': email,
          'phone': phone,
          'password': password,
          'vehicleType': vehicleType,
          'licensePlate': licensePlate,
        },
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => AuthResponseModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        await _saveTokens(apiResponse.data!);
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Logout
  Future<void> logout() async {
    try {
      final refreshToken = await _storage.read(
        key: ApiConstants.refreshTokenKey,
      );

      if (refreshToken != null) {
        await _dioClient.post(
          ApiConstants.logout,
          data: {'refreshToken': refreshToken},
        );
      }
    } catch (e) {
      // Ignore errors on logout
    } finally {
      await clearTokens();
    }
  }

  /// Save tokens to secure storage
  Future<void> _saveTokens(AuthResponseModel authResponse) async {
    await _storage.write(
      key: ApiConstants.accessTokenKey,
      value: authResponse.accessToken,
    );
    await _storage.write(
      key: ApiConstants.refreshTokenKey,
      value: authResponse.refreshToken,
    );
    await _storage.write(
      key: ApiConstants.userRoleKey,
      value: authResponse.user.role,
    );
    await _storage.write(
      key: ApiConstants.userIdKey,
      value: authResponse.user.id,
    );
  }

  /// Clear all tokens
  Future<void> clearTokens() async {
    await _storage.delete(key: ApiConstants.accessTokenKey);
    await _storage.delete(key: ApiConstants.refreshTokenKey);
    await _storage.delete(key: ApiConstants.userRoleKey);
    await _storage.delete(key: ApiConstants.userIdKey);
  }

  /// Check if user is logged in
  Future<bool> isLoggedIn() async {
    final accessToken = await _storage.read(key: ApiConstants.accessTokenKey);
    return accessToken != null && accessToken.isNotEmpty;
  }

  /// Get user role
  Future<String?> getUserRole() async {
    return await _storage.read(key: ApiConstants.userRoleKey);
  }

  /// Get user ID
  Future<String?> getUserId() async {
    return await _storage.read(key: ApiConstants.userIdKey);
  }
}
