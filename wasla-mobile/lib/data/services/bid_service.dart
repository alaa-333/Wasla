import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/bid_model.dart';
import '../models/job_model.dart';
import '../models/api_response_model.dart';

class BidService {
  final DioClient _dioClient = DioClient.instance;

  /// Submit Bid (DRIVER only)
  Future<BidModel> submitBid({
    required String jobId,
    required double price,
    String? note,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.submitBid(jobId),
        data: {
          'price': price,
          if (note != null && note.isNotEmpty) 'note': note,
        },
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => BidModel.fromJson(data),
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

  /// Get All Bids for a Job
  Future<List<BidModel>> getJobBids(String jobId) async {
    try {
      final response = await _dioClient.get(ApiConstants.jobBids(jobId));

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) =>
            (data as List).map((item) => BidModel.fromJson(item)).toList(),
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

  /// Accept Bid (CLIENT only)
  Future<JobModel> acceptBid({
    required String jobId,
    required String bidId,
  }) async {
    try {
      final response = await _dioClient.patch(
        ApiConstants.acceptBid(jobId, bidId),
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

  /// Get Driver's Bids (DRIVER only)
  Future<List<BidModel>> getMyBids() async {
    try {
      final response = await _dioClient.get(ApiConstants.driverBids);

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) =>
            (data as List).map((item) => BidModel.fromJson(item)).toList(),
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
