import 'package:dio/dio.dart';
import '../../core/constants/api_constants.dart';
import '../../core/network/dio_client.dart';
import '../../core/network/api_exception.dart';
import '../models/rating_model.dart';
import '../models/api_response_model.dart';

class RatingService {
  final DioClient _dioClient = DioClient.instance;

  /// Submit Rating (CLIENT only)
  Future<RatingModel> submitRating({
    required String jobId,
    required int score,
    String? comment,
  }) async {
    try {
      final response = await _dioClient.post(
        ApiConstants.submitRating(jobId),
        data: {
          'score': score,
          if (comment != null && comment.isNotEmpty) 'comment': comment,
        },
      );

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => RatingModel.fromJson(data),
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

  /// Get Rating for a Job
  Future<RatingModel?> getRating(String jobId) async {
    try {
      final response = await _dioClient.get(ApiConstants.getRating(jobId));

      final apiResponse = ApiResponse.fromJson(
        response.data,
        (data) => RatingModel.fromJson(data),
      );

      if (apiResponse.success) {
        return apiResponse.data;
      } else {
        return null;
      }
    } on DioException catch (e) {
      // Return null if rating doesn't exist (404)
      if (e.response?.statusCode == 404) {
        return null;
      }
      throw ApiException.fromDioError(e);
    }
  }
}
