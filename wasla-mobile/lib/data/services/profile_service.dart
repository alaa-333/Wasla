import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/client_profile_model.dart';
import '../models/driver_profile_model.dart';
import '../models/api_response_model.dart';

class ProfileService {
  final DioClient _dioClient = DioClient.instance;

  // ============ CLIENT PROFILE ============

  /// Get Client Profile
  Future<ClientProfileModel> getClientProfile() async {
    try {
      final response = await _dioClient.get(ApiConstants.clientProfile);

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => ClientProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Update Client Profile
  Future<ClientProfileModel> updateClientProfile({
    required String fullName,
    required String phone,
  }) async {
    try {
      final response = await _dioClient.put(
        ApiConstants.updateClientProfile,
        data: {'fullName': fullName, 'phone': phone},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => ClientProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  // ============ DRIVER PROFILE ============

  /// Get Driver Profile
  Future<DriverProfileModel> getDriverProfile() async {
    try {
      final response = await _dioClient.get(ApiConstants.driverProfile);

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => DriverProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Update Driver Profile
  Future<DriverProfileModel> updateDriverProfile({
    required String fullName,
    required String phone,
  }) async {
    try {
      final response = await _dioClient.put(
        ApiConstants.updateDriverProfile,
        data: {'fullName': fullName, 'phone': phone},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => DriverProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Update Driver Availability Status
  Future<DriverProfileModel> updateDriverStatus({
    required bool isAvailable,
  }) async {
    try {
      final response = await _dioClient.patch(
        ApiConstants.updateDriverStatus,
        data: {'isAvailable': isAvailable},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => DriverProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Update Driver Location
  Future<void> updateDriverLocation({
    required double lat,
    required double lng,
  }) async {
    try {
      await _dioClient.put(
        ApiConstants.updateDriverLocation,
        data: {'lat': lat, 'lng': lng},
      );
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }

  /// Get Driver Public Profile
  Future<DriverProfileModel> getDriverPublicProfile(String driverId) async {
    try {
      final response = await _dioClient.get(
        ApiConstants.driverPublicProfile(driverId),
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => DriverProfileModel.fromJson(data),
      );

      if (apiResponse.success && apiResponse.data != null) {
        return apiResponse.data!;
      } else {
        throw ApiException(message: apiResponse.message);
      }
    } on DioException catch (e) {
      throw ApiException.fromDioError(e);
    }
  }
}
