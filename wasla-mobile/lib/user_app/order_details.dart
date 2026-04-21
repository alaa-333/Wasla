import 'package:flutter/material.dart';
import 'package:latlong2/latlong.dart';
import '../core/theme/app_colors.dart';
import '../core/theme/app_styles.dart';
import '../core/api/api_client.dart';
import 'bids_page.dart';

class OrderDetails extends StatefulWidget {
  final String userName;
  final LatLng pickUp;
  final LatLng dropOff;
  final String pickUpAddress;
  final String dropOffAddress;

  const OrderDetails({
    super.key,
    required this.userName,
    required this.pickUp,
    required this.dropOff,
    required this.pickUpAddress,
    required this.dropOffAddress,
  });

  @override
  State<OrderDetails> createState() => _OrderDetailsState();
}

class _OrderDetailsState extends State<OrderDetails> {
  final TextEditingController _noteController = TextEditingController();
  bool _isLoading = false;

  // 🚀 الدالة الفعلية لإرسال الطلب (نصي فقط - بدون صور)
  Future<void> _sendOrderToServer() async {
    if (_noteController.text.isEmpty) {
      _showSnackBar("برجاء إضافة وصف للشحنة", Colors.orange);
      return;
    }

    setState(() => _isLoading = true);

    try {
      // إرسال طلب إنشاء وظيفة: POST /api/v1/jobs
      final response = await ApiClient.post(
        '/jobs',
        body: {
          'pickupAddress': widget.pickUpAddress,
          'pickupLat': widget.pickUp.latitude,
          'pickupLng': widget.pickUp.longitude,
          'dropoffAddress': widget.dropOffAddress,
          'dropoffLat': widget.dropOff.latitude,
          'dropoffLng': widget.dropOff.longitude,
          'cargoDesc': _noteController.text,
        },
      );

      if (response.success && response.data != null) {
        final jobId = response.data['data']['id'];

        if (mounted) {
          _showSnackBar(
            "تم إرسال طلبك بنجاح! جاري البحث عن عروض..",
            Colors.green,
          );

          // الانتقال لصفحة العروض مع jobId
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => BidsPage(jobId: jobId)),
          );
        }
      } else {
        _showSnackBar(response.message, Colors.red);
      }
    } catch (e) {
      _showSnackBar("خطأ في الاتصال بالشبكة: $e", Colors.red);
    } finally {
      if (mounted) setState(() => _isLoading = false);
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
        appBar: AppBar(
          title: const Text("تفاصيل الشحنة"),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          centerTitle: true,
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildSectionTitle("وصف الشحنة ✍️"),
              const SizedBox(height: 10),
              _buildNoteField(),
              const SizedBox(height: 50),
              _buildSubmitButton(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontWeight: FontWeight.bold,
        fontSize: 17,
        color: AppColors.primary,
      ),
    );
  }

  Widget _buildSubmitButton() {
    return SizedBox(
      width: double.infinity,
      height: 60,
      child: ElevatedButton(
        onPressed: _isLoading ? null : _sendOrderToServer,
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15),
          ),
          elevation: 5,
        ),
        child: _isLoading
            ? const CircularProgressIndicator(color: Colors.white)
            : const Text("اطلب الآن 🚀", style: AppStyles.buttonText),
      ),
    );
  }

  Widget _buildNoteField() {
    return TextField(
      controller: _noteController,
      maxLines: 4,
      decoration: InputDecoration(
        hintText: "مثلاً: ثلاجة، غسالة، غرفتين نوم..",
        hintStyle: const TextStyle(fontSize: 14, color: Colors.grey),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(15),
          borderSide: BorderSide(color: Colors.grey[300]!),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(15),
          borderSide: const BorderSide(color: AppColors.primary, width: 2),
        ),
      ),
    );
  }
}
