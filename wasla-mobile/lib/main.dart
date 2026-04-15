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
      },
    );
  }
}
