import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../core/api/api_client.dart';
import '../core/api/auth_storage.dart';
import 'user_home.dart';

class UserRegistration extends StatefulWidget {
  const UserRegistration({super.key});

  @override
  State<UserRegistration> createState() => _UserRegistrationState();
}

class _UserRegistrationState extends State<UserRegistration> {
  final TextEditingController _nameController = TextEditingController();
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _phoneController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();

  bool _isLoading = false; // 🔄 حالة التحميل

  // 🛡️ دالة التسجيل الفعلية حسب الدليل v3.1 (نقطة 5.1)
  void _handleRegister() async {
    // 1. تحقق بسيط من البيانات
    if (_nameController.text.isEmpty ||
        _emailController.text.isEmpty ||
        _phoneController.text.isEmpty ||
        _passwordController.text.isEmpty) {
      _showError("برجاء ملء جميع الحقول");
      return;
    }

    // 2. التحقق من صيغة الهاتف (الدليل يشترط وجود رمز الدولة +)
    if (!_phoneController.text.startsWith('+')) {
      _showError("برجاء إدخال رقم الهاتف مع رمز الدولة (مثلاً +20)");
      return;
    }

    setState(() => _isLoading = true);

    try {
      // إرسال طلب التسجيل: POST /api/v1/auth/register/client
      final response = await ApiClient.post(
        '/auth/register/client',
        body: {
          'fullName': _nameController.text,
          'email': _emailController.text,
          'phone': _phoneController.text,
          'password': _passwordController.text,
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
          // الانتقال للشاشة الرئيسية
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(
              builder: (c) => UserHome(userName: _nameController.text),
            ),
          );
        }
      } else {
        _showError(response.message);
      }
    } catch (e) {
      _showError("خطأ في الاتصال بالسيرفر");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: Colors.red),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        backgroundColor: const Color(0xFFF9F9F9),
        appBar: AppBar(
          backgroundColor: Colors.transparent,
          elevation: 0,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back_ios, color: AppColors.primary),
            onPressed: () => Navigator.pop(context),
          ),
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 30),
          child: Column(
            children: [
              const SizedBox(height: 20),
              const Text(
                "إنشاء حساب جديد",
                style: TextStyle(
                  fontSize: 30,
                  fontWeight: FontWeight.bold,
                  color: AppColors.primary,
                ),
              ),
              const SizedBox(height: 50),

              _buildInputField(
                _nameController,
                "الاسم الكامل",
                Icons.person_outline,
              ),
              const SizedBox(height: 20),

              _buildInputField(
                _emailController,
                "البريد الإلكتروني",
                Icons.email_outlined,
                keyboardType: TextInputType.emailAddress,
              ),
              const SizedBox(height: 20),

              _buildInputField(
                _phoneController,
                "رقم الهاتف (مثلاً +2010...)",
                Icons.phone_android_outlined,
                isPhone: true,
              ),
              const SizedBox(height: 20),

              _buildInputField(
                _passwordController,
                "كلمة المرور",
                Icons.lock_outline,
                isPassword: true,
              ),

              const SizedBox(height: 60),

              // زر تسجيل مع حالة التحميل
              SizedBox(
                width: double.infinity,
                height: 65,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _handleRegister,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(25),
                    ),
                  ),
                  child: _isLoading
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text(
                          "تسجيل",
                          style: TextStyle(
                            fontSize: 20,
                            color: Colors.white,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
              const SizedBox(height: 30),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInputField(
    TextEditingController controller,
    String hint,
    IconData icon, {
    bool isPassword = false,
    bool isPhone = false,
    TextInputType keyboardType = TextInputType.text,
  }) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.03),
            blurRadius: 15,
            offset: const Offset(0, 5),
          ),
        ],
      ),
      child: TextField(
        controller: controller,
        obscureText: isPassword,
        keyboardType: isPhone ? TextInputType.phone : keyboardType,
        decoration: InputDecoration(
          hintText: hint,
          hintStyle: const TextStyle(color: Colors.grey, fontSize: 14),
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
}
