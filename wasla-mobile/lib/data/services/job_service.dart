import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/job_model.dart';
import '../models/api_response_model.dart';

class JobService {
  final DioClient _dioClient = DioClient.instance;

  /// Create Job (CLIENT only)
  Future<JobModel> createJob({
    required String pickupAddress,
    required double pickupLat,
    required double pickupLng,
    required String dropoffAddress,
    required double dropoffLat,
    required double dropoffLng,
    required String cargoDesc,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.jobs,
        data: {
          'pickupAddress': pickupAddress,
          'pickupLat': pickupLat,
          'pickupLng': pickupLng,
          'dropoffAddress': dropoffAddress,
          'dropoffLat': dropoffLat,
          'dropoffLng': dropoffLng,
          'cargoDesc': cargoDesc,
        },
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => JobModel.fromJson(data),
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

  /// Get Job Details
  Future<JobModel> getJobDetails(String jobId) async {
    try {
      final response = await _dioClient.get(ApiConstants.jobDetails(jobId));

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => JobModel.fromJson(data),
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

  /// Get My Jobs (with pagination)
  Future<PaginatedResponse<JobModel>> getMyJobs({
    int page = 0,
    int size = 20,
  }) async {
    try {
      final response = await _dioClient.get(
        ApiConstants.myJobs,
        queryParameters: {'page': page, 'size': size},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) =>
            PaginatedResponse.fromJson(data, (json) => JobModel.fromJson(json)),
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

  /// Get Nearby Jobs (DRIVER only)
  Future<List<JobModel>> getNearbyJobs({
    required double lat,
    required double lng,
    double radiusKm = 15,
  }) async {
    try {
      final response = await _dioClient.get(
        ApiConstants.nearbyJobs,
        queryParameters: {'lat': lat, 'lng': lng, 'radiusKm': radiusKm},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) =>
            (data as List).map((item) => JobModel.fromJson(item)).toList(),
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

  /// Update Job Status (DRIVER only)
  Future<JobModel> updateJobStatus({
    required String jobId,
    required String status,
  }) async {
    try {
      final response = await _dioClient.patch(
        ApiConstants.updateJobStatus(jobId),
        data: {'status': status},
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => JobModel.fromJson(data),
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
