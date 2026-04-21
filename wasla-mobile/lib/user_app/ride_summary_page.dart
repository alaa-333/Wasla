import 'package:flutter/material.dart';
import '../core/theme/app_colors.dart';
import '../core/theme/app_styles.dart';
// import '../services/job_service.dart'; // سنحتاجه لإرسال التقييم

class RideSummaryPage extends StatefulWidget {
  final String jobId; // تغيير المسمى لـ jobId ليتطابق مع الدليل
  final String driverName;
  final double bidPrice;
  final double distance;

  const RideSummaryPage({
    super.key,
    required this.jobId,
    this.driverName = "الأسطى عبده",
    this.bidPrice = 280.0,
    this.distance = 12.5,
  });

  @override
  State<RideSummaryPage> createState() => _RideSummaryPageState();
}

class _RideSummaryPageState extends State<RideSummaryPage> {
  int _userRating = 0;
  bool _isSubmitting = false;

  @override
  void initState() {
    super.initState();
  }

  // ⭐️ دالة إرسال التقييم حسب الدليل (نقطة 5.6)
  Future<void> _submitRatingAndFinish() async {
    if (_userRating == 0) {
      _showSnackBar("من فضلك قيم الكابتن أولاً", Colors.orange);
      return;
    }

    setState(() => _isSubmitting = true);

    // محاكاة إرسال التقييم: POST /api/v1/ratings
    // Body: { "jobId": widget.jobId, "score": _userRating, "comment": "" }
    await Future.delayed(const Duration(seconds: 1));

    if (mounted) {
      setState(() => _isSubmitting = false);

      // العودة للبداية بعد النجاح
      Navigator.of(context).popUntil((route) => route.isFirst);
      _showSnackBar("تم إنهاء الرحلة بنجاح. شكراً لك! ❤️", Colors.green);
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
        backgroundColor: Colors.grey[50],
        appBar: AppBar(
          title: const Text("ملخص الرحلة"),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          centerTitle: true,
          automaticallyImplyLeading: false,
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            children: [
              const Icon(Icons.check_circle, color: Colors.green, size: 80),
              const SizedBox(height: 10),
              const Text("وصلت بالسلامة! 🎉", style: AppStyles.mainTitle),
              const Text(
                "تم توصيل عفشك بنجاح مع وصلة",
                style: TextStyle(color: Colors.grey),
              ),
              const SizedBox(height: 30),

              // كارت الفاتورة
              _buildInvoiceCard(),

              const SizedBox(height: 25),

              // تقييم النجوم
              const Text(
                "كيف كانت تجربتك مع الكابتن؟",
                style: AppStyles.bodyText,
              ),
              const SizedBox(height: 10),
              _buildRatingStars(),

              const SizedBox(height: 40),

              // زر الإنهاء
              SizedBox(
                width: double.infinity,
                height: 60,
                child: ElevatedButton(
                  onPressed: _isSubmitting ? null : _submitRatingAndFinish,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: AppColors.primary,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(15),
                    ),
                  ),
                  child: _isSubmitting
                      ? const CircularProgressIndicator(color: Colors.white)
                      : const Text(
                          "تأكيد وإنهاء الرحلة",
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildInvoiceCard() {
    return Container(
      padding: const EdgeInsets.all(20),
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
      child: Column(
        children: [
          _buildSummaryRow("رقم الرحلة", widget.jobId),
          const Divider(height: 25),
          _buildSummaryRow("اسم الكابتن", widget.driverName),
          const Divider(height: 25),
          _buildSummaryRow("المسافة المقطوعة", "${widget.distance} كم"),
          const Divider(height: 25, thickness: 1.5),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                "إجمالي المبلغ",
                style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
              ),
              Text(
                "${widget.bidPrice.toStringAsFixed(2)} ج.م",
                style: const TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 24,
                  color: AppColors.primary,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildRatingStars() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: List.generate(5, (index) {
        return IconButton(
          icon: Icon(
            index < _userRating ? Icons.star : Icons.star_border,
            color: Colors.orange,
            size: 45,
          ),
          onPressed: () => setState(() => _userRating = index + 1),
        );
      }),
    );
  }

  Widget _buildSummaryRow(String title, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(title, style: const TextStyle(color: Colors.grey, fontSize: 15)),
        Text(
          value,
          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w500),
        ),
      ],
    );
  }
}
