import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../core/theme/app_styles.dart';
import '../core/api/api_client.dart';
import '../core/api/auth_storage.dart';
import 'driver_home.dart';

class DriverRegistrationScreen extends StatefulWidget {
  const DriverRegistrationScreen({super.key});

  @override
  State<DriverRegistrationScreen> createState() =>
      _DriverRegistrationScreenState();
}

class _DriverRegistrationScreenState extends State<DriverRegistrationScreen> {
  bool _isUploading = false;

  final TextEditingController emailController = TextEditingController();
  final TextEditingController nameController = TextEditingController();
  final TextEditingController phoneController = TextEditingController();
  final TextEditingController vehicleTypeController = TextEditingController();
  final TextEditingController licensePlateController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  // 🚀 إرسال طلب التسجيل طبقاً للدليل 5.1 - تسجيل سائق
  Future<void> _registerDriver() async {
    if (nameController.text.isEmpty ||
        emailController.text.isEmpty ||
        phoneController.text.isEmpty ||
        passwordController.text.isEmpty ||
        vehicleTypeController.text.isEmpty ||
        licensePlateController.text.isEmpty) {
      _showSnackBar("يرجى إكمال كافة البيانات المطلوبة", Colors.orange);
      return;
    }

    // التحقق من صيغة الهاتف
    if (!phoneController.text.startsWith('+')) {
      _showSnackBar(
        "برجاء إدخال رقم الهاتف مع رمز الدولة (مثلاً +20)",
        Colors.orange,
      );
      return;
    }

    setState(() => _isUploading = true);

    try {
      // إرسال طلب التسجيل: POST /api/v1/auth/register/driver
      final response = await ApiClient.post(
        '/auth/register/driver',
        body: {
          'fullName': nameController.text,
          'email': emailController.text,
          'phone': phoneController.text,
          'password': passwordController.text,
          'vehicleType': vehicleTypeController.text.toUpperCase(),
          'licensePlate': licensePlateController.text,
        },
        requiresAuth: false,
      );

      if (response.success && response.data != null) {
        final data = response.data['data'];

        // حفظ الـ Tokens
        await AuthStorage.saveTokens(
          accessToken: data['accessToken'],
          refreshToken: data['refreshToken'],
        );

        // حفظ بيانات المستخدم
        final user = data['user'];
        await AuthStorage.saveUserData(
          userId: user['id'],
          role: user['role'],
          name: user['fullName'] ?? user['email'],
          email: user['email'],
          phone: user['phone'],
        );

        if (mounted) {
          _showSnackBar("تم التسجيل بنجاح! مرحباً بك في وصلة ✅", Colors.green);
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (c) => const DriverHome()),
          );
        }
      } else {
        _showSnackBar(response.message, Colors.red);
      }
    } catch (e) {
      _showSnackBar("خطأ في الاتصال بالسيرفر، تأكد من الإنترنت", Colors.red);
    } finally {
      if (mounted) setState(() => _isUploading = false);
    }
  }

  void _showSnackBar(String msg, Color color) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(msg), backgroundColor: color));
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        backgroundColor: const Color(0xFFF9F9F9),
        appBar: AppBar(
          backgroundColor: Colors.white,
          elevation: 0,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back_ios, color: AppColors.primary),
            onPressed: () => Navigator.pop(context),
          ),
          title: const Text(
            "تسجيل كابتن جديد",
            style: TextStyle(
              color: AppColors.primary,
              fontWeight: FontWeight.bold,
            ),
          ),
          centerTitle: true,
        ),
        body: SingleChildScrollView(
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 25),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 20),
                const Text("معلومات الكابتن", style: AppStyles.subTitle),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "الاسم بالكامل",
                  Icons.person_outline,
                  nameController,
                ),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "البريد الإلكتروني",
                  Icons.email_outlined,
                  emailController,
                ),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "رقم الهاتف (مثلاً +2010...)",
                  Icons.phone_android_outlined,
                  phoneController,
                ),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "كلمة المرور",
                  Icons.lock_outline,
                  passwordController,
                ),

                const SizedBox(height: 30),
                const Text("بيانات الشاحنة", style: AppStyles.subTitle),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "نوع الشاحنة (مثلاً PICKUP_ONE_TON)",
                  Icons.local_shipping_outlined,
                  vehicleTypeController,
                ),
                const SizedBox(height: 15),
                _buildDriverInput(
                  "رقم اللوحة",
                  Icons.tag,
                  licensePlateController,
                ),

                const SizedBox(height: 30),
                const Text(
                  "ملاحظة: رفع المستندات سيكون متاحاً قريباً",
                  style: TextStyle(color: Colors.grey, fontSize: 14),
                ),

                const SizedBox(height: 50),
                _buildSubmitButton(),
                const SizedBox(height: 40),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildDriverInput(
    String hint,
    IconData icon,
    TextEditingController controller,
  ) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            blurRadius: 10,
          ),
        ],
      ),
      child: TextField(
        controller: controller,
        decoration: InputDecoration(
          hintText: hint,
          prefixIcon: Icon(icon, color: AppColors.primary),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(
            vertical: 20,
            horizontal: 20,
          ),
        ),
      ),
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity,
      height: 65,
      child: ElevatedButton(
        // ربط الزرار بدالة الرفع وإظهار علامة التحميل
        onPressed: _isUploading ? null : _registerDriver,
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(20),
          ),
          elevation: 5,
        ),
        child: _isUploading
            ? const CircularProgressIndicator(color: Colors.white)
            : const Text("تقديم طلب الانضمام", style: AppStyles.buttonText),
      ),
    );
  }
}
