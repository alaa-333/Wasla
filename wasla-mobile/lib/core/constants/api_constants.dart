import '../config/app_config.dart';

/// API Constants for Wasla Backend
class ApiConstants {
  // Base URLs - Now using centralized AppConfig
  static String get baseUrl => AppConfig.baseUrl;
  static String get apiVersion => AppConfig.apiVersion;
  static String get baseApiUrl => AppConfig.apiBaseUrl;

  // WebSocket
  static String get wsUrl => AppConfig.wsUrl;

  // Auth Endpoints
  static const String login = '/auth/login';
  static const String registerClient = '/auth/register/client';
  static const String registerDriver = '/auth/register/driver';
  static const String refreshToken = '/auth/refresh';
  static const String logout = '/auth/logout';

  // Jobs Endpoints
  static const String jobs = '/jobs';
  static const String myJobs = '/jobs/my';
  static const String nearbyJobs = '/jobs/nearby';
  static String jobDetails(String jobId) => '/jobs/$jobId';
  static String updateJobStatus(String jobId) => '/jobs/$jobId/status';

  // Bids Endpoints
  static String jobBids(String jobId) => '/jobs/$jobId/bids';
  static String submitBid(String jobId) => '/jobs/$jobId/bids';
  static String acceptBid(String jobId, String bidId) =>
      '/jobs/$jobId/bids/$bidId/accept';

  // Client Endpoints
  static const String clientProfile = '/clients/me';
  static const String updateClientProfile = '/clients/me/profile';
  static const String updateClientFcm = '/clients/me/fcm-token';

  // Driver Endpoints
  static const String driverProfile = '/drivers/me';
  static const String updateDriverProfile = '/drivers/me/profile';
  static const String updateDriverStatus = '/drivers/me/status';
  static const String updateDriverLocation = '/drivers/me/location';
  static const String updateDriverFcm = '/drivers/me/fcm-token';
  static const String driverBids = '/drivers/me/bids';
  static String driverPublicProfile(String driverId) => '/drivers/$driverId';

  // Rating Endpoints
  static String submitRating(String jobId) => '/jobs/$jobId/rating';
  static String getRating(String jobId) => '/jobs/$jobId/rating';

  // WebSocket Destinations
  static const String wsDriverLocation = '/app/driver.location';
  static String wsJobLocationTopic(String jobId) =>
      '/topic/job/$jobId/location';

  // Token Keys
  static const String accessTokenKey = 'access_token';
  static const String refreshTokenKey = 'refresh_token';
  static const String userRoleKey = 'user_role';
  static const String userIdKey = 'user_id';
}
