import 'dart:io';
import 'package:flutter/foundation.dart' show kIsWeb;

/// App Configuration - Centralized API URL Management
/// Automatically detects platform and uses correct base URL
class AppConfig {
  // ⚠️ IMPORTANT: Change this to your actual backend URL in production
  static const String _productionUrl = 'https://wasla-api.up.railway.app';

  // Local development URLs
  static const String _localhostUrl = 'http://localhost:8080';
  static const String _androidEmulatorUrl = 'http://10.0.2.2:8080';

  // 🔧 FOR PHYSICAL DEVICE: Uncomment and set your local IP
  // Find your IP: Windows (ipconfig), Mac/Linux (ifconfig)
  // static const String _physicalDeviceUrl = 'http://192.168.1.100:8080';
  static const String? _physicalDeviceUrl = null;

  /// Get the correct base URL based on platform and environment
  static String get baseUrl {
    // Use production URL if set
    if (_useProduction) {
      return _productionUrl;
    }

    // Web (Chrome, Firefox, etc.)
    if (kIsWeb) {
      return _localhostUrl;
    }

    // Physical device (if configured)
    if (_physicalDeviceUrl != null) {
      return _physicalDeviceUrl!;
    }

    // Android Emulator
    if (Platform.isAndroid) {
      return _androidEmulatorUrl;
    }

    // iOS Simulator / Other platforms
    return _localhostUrl;
  }

  /// API version
  static const String apiVersion = '/api/v1';

  /// Full API base URL
  static String get apiBaseUrl => '$baseUrl$apiVersion';

  /// WebSocket URL
  static String get wsUrl {
    final wsProtocol = baseUrl.startsWith('https') ? 'wss' : 'ws';
    final urlWithoutProtocol = baseUrl.replaceAll(RegExp(r'https?://'), '');
    return '$wsProtocol://$urlWithoutProtocol/ws';
  }

  /// Toggle between production and local development
  /// Set to true when deploying to production
  static const bool _useProduction = false;

  /// Connection timeout in seconds
  static const int connectionTimeout = 30;

  /// Receive timeout in seconds
  static const int receiveTimeout = 30;

  /// Print current configuration (for debugging)
  static void printConfig() {
    print('🌐 App Configuration:');
    print('   Platform: ${_getPlatformName()}');
    print('   Base URL: $baseUrl');
    print('   API URL: $apiBaseUrl');
    print('   WebSocket: $wsUrl');
    print('   Production Mode: $_useProduction');
  }

  static String _getPlatformName() {
    if (kIsWeb) return 'Web';
    if (Platform.isAndroid) return 'Android';
    if (Platform.isIOS) return 'iOS';
    if (Platform.isWindows) return 'Windows';
    if (Platform.isMacOS) return 'macOS';
    if (Platform.isLinux) return 'Linux';
    return 'Unknown';
  }
}
