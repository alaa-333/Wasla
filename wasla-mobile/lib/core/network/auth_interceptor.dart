import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import '../constants/api_constants.dart';

/// Auth Interceptor for handling JWT tokens and auto-refresh
class AuthInterceptor extends Interceptor {
  final Dio _dio;
  final FlutterSecureStorage _storage;
  bool _isRefreshing = false;
  final List<RequestOptions> _requestQueue = [];

  AuthInterceptor(this._dio, this._storage);

  @override
  void onRequest(
    RequestOptions options,
    RequestInterceptorHandler handler,
  ) async {
    // Skip auth for public endpoints
    if (_isPublicEndpoint(options.path)) {
      return handler.next(options);
    }

    // Add access token to header
    final accessToken = await _storage.read(key: ApiConstants.accessTokenKey);
    if (accessToken != null && accessToken.isNotEmpty) {
      options.headers['Authorization'] = 'Bearer $accessToken';
    }

    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    // Handle 401 Unauthorized - token expired
    if (err.response?.statusCode == 401 &&
        !_isPublicEndpoint(err.requestOptions.path)) {
      if (!_isRefreshing) {
        _isRefreshing = true;

        try {
          // Try to refresh token
          final newAccessToken = await _refreshToken();

          if (newAccessToken != null) {
            // Retry original request with new token
            err.requestOptions.headers['Authorization'] =
                'Bearer $newAccessToken';

            // Retry all queued requests
            for (var request in _requestQueue) {
              request.headers['Authorization'] = 'Bearer $newAccessToken';
            }
            _requestQueue.clear();

            final response = await _dio.fetch(err.requestOptions);
            _isRefreshing = false;
            return handler.resolve(response);
          } else {
            // Refresh failed - logout user
            await _clearTokens();
            _isRefreshing = false;
            return handler.reject(err);
          }
        } catch (e) {
          _isRefreshing = false;
          await _clearTokens();
          return handler.reject(err);
        }
      } else {
        // Queue request while refreshing
        _requestQueue.add(err.requestOptions);
      }
    }

    return handler.next(err);
  }

  /// Refresh access token using refresh token
  Future<String?> _refreshToken() async {
    try {
      final refreshToken = await _storage.read(
        key: ApiConstants.refreshTokenKey,
      );

      if (refreshToken == null || refreshToken.isEmpty) {
        return null;
      }

      final response = await _dio.post(
        ApiConstants.refreshToken,
        data: {'refreshToken': refreshToken},
        options: Options(
          headers: {'Authorization': null}, // Remove auth header
        ),
      );

      if (response.statusCode == 200 && response.data['success'] == true) {
        final data = response.data['data'];
        final newAccessToken = data['accessToken'];
        final newRefreshToken = data['refreshToken'];

        // Store new tokens
        await _storage.write(
          key: ApiConstants.accessTokenKey,
          value: newAccessToken,
        );
        await _storage.write(
          key: ApiConstants.refreshTokenKey,
          value: newRefreshToken,
        );

        return newAccessToken;
      }

      return null;
    } catch (e) {
      return null;
    }
  }

  /// Clear all stored tokens
  Future<void> _clearTokens() async {
    await _storage.delete(key: ApiConstants.accessTokenKey);
    await _storage.delete(key: ApiConstants.refreshTokenKey);
    await _storage.delete(key: ApiConstants.userRoleKey);
    await _storage.delete(key: ApiConstants.userIdKey);
  }

  /// Check if endpoint is public (doesn't require auth)
  bool _isPublicEndpoint(String path) {
    final publicPaths = [
      ApiConstants.login,
      ApiConstants.registerClient,
      ApiConstants.registerDriver,
      ApiConstants.refreshToken,
    ];

    return publicPaths.any((publicPath) => path.contains(publicPath));
  }
}
