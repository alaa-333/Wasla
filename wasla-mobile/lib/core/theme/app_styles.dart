import 'package:flutter/material.dart';
import 'app_colors.dart';

class AppStyles {
  // ستايل اللوجو
  static const TextStyle logoStyle = TextStyle(
    fontSize: 35,
    fontWeight: FontWeight.bold,
    color: AppColors.primary,
    fontFamily: 'Cairo', // تأكد من إضافة الخط في pubspec.yaml
    height: 1.2,
  );

  static const TextStyle mainTitle = TextStyle(
    fontSize: 22,
    fontWeight: FontWeight.bold,
    fontFamily: 'Cairo',
    color: AppColors.primary,
  );

  static const TextStyle subTitle = TextStyle(
    fontSize: 18,
    color: AppColors.textPrimary,
    fontWeight: FontWeight.w600,
    fontFamily: 'Cairo',
  );

  static const TextStyle bodyText = TextStyle(
    fontSize: 16,
    fontFamily: 'Cairo',
    color: AppColors.textPrimary,
  );

  static const TextStyle buttonText = TextStyle(
    fontSize: 18,
    fontWeight: FontWeight.bold,
    fontFamily: 'Cairo',
    color: Colors.white,
  );

  static const TextStyle hintText = TextStyle(
    color: AppColors.textSecondary,
    fontFamily: 'Cairo',
    fontSize: 14,
  );
}
