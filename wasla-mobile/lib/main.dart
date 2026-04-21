import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'core/theme/app_theme.dart';
import 'core/config/app_config.dart';
import 'presentation/screens/splash/splash_screen.dart';
import 'presentation/screens/auth/role_selection_screen.dart';
import 'presentation/screens/auth/login_screen.dart';
import 'presentation/screens/auth/client_register_screen.dart';
import 'presentation/screens/auth/driver_register_screen.dart';
import 'presentation/screens/client/client_home_screen.dart';
import 'presentation/screens/driver/driver_home_screen.dart';
import 'presentation/screens/debug/connection_test_screen.dart';

// Import old screens (Legacy)
import 'user_app/user_home.dart';
import 'user_app/order_details.dart';
import 'user_app/bids_page.dart';
import 'user_app/user_tracking_page.dart';
import 'user_app/ride_summary_page.dart';
import 'driver_app/driver_home.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();

  // Print app configuration for debugging
  AppConfig.printConfig();

  // Set system UI overlay style
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.dark,
    ),
  );

  runApp(const WaslaApp());
}

class WaslaApp extends StatelessWidget {
  const WaslaApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'وصلة - Wasla',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      initialRoute: '/',
      routes: {
        '/': (context) => const SplashScreen(),
        '/role-selection': (context) => const RoleSelectionScreen(),
        '/login': (context) => const LoginScreen(),
        '/register/client': (context) => const ClientRegisterScreen(),
        '/register/driver': (context) => const DriverRegisterScreen(),
        '/client/home': (context) => const ClientHomeScreen(),
        '/driver/home': (context) => const DriverHomeScreen(),
        '/debug/connection-test': (context) => const ConnectionTestScreen(),

        // Legacy screens (Old system - fully functional)
        // These screens are complete and working, just need to be integrated
        // TODO: Migrate these to presentation/screens/ for better architecture
      },

      // Handle routes with parameters using onGenerateRoute
      onGenerateRoute: (settings) {
        // Client flow routes
        if (settings.name == '/client/create-job') {
          final args = settings.arguments as Map<String, dynamic>?;
          return MaterialPageRoute(
            builder: (context) =>
                UserHome(userName: args?['userName'] ?? 'مستخدم'),
          );
        }

        if (settings.name == '/client/job-details') {
          final args = settings.arguments as Map<String, dynamic>;
          return MaterialPageRoute(
            builder: (context) => OrderDetails(
              userName: args['userName'],
              pickUp: args['pickUp'],
              dropOff: args['dropOff'],
              pickUpAddress: args['pickUpAddress'],
              dropOffAddress: args['dropOffAddress'],
            ),
          );
        }

        if (settings.name == '/client/bids') {
          final args = settings.arguments as Map<String, dynamic>;
          return MaterialPageRoute(
            builder: (context) => BidsPage(jobId: args['jobId']),
          );
        }

        if (settings.name == '/client/tracking') {
          final args = settings.arguments as Map<String, dynamic>;
          return MaterialPageRoute(
            builder: (context) => UserTrackingPage(jobId: args['jobId']),
          );
        }

        if (settings.name == '/client/rate') {
          final args = settings.arguments as Map<String, dynamic>;
          return MaterialPageRoute(
            builder: (context) => RideSummaryPage(
              jobId: args['jobId'],
              driverName: args['driverName'] ?? 'السائق',
              bidPrice: args['bidPrice'] ?? 0.0,
              distance: args['distance'] ?? 0.0,
            ),
          );
        }

        // Driver flow routes
        if (settings.name == '/driver/jobs') {
          return MaterialPageRoute(builder: (context) => const DriverHome());
        }

        return null;
      },
    );
  }
}
