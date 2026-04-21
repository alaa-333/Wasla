import 'package:flutter/material.dart';
import 'dart:async';
import '../core/theme/app_colors.dart';
import '../core/api/api_client.dart';
import 'user_tracking_page.dart';

class BidsPage extends StatefulWidget {
  final String jobId; // رقم الوظيفة (UUID)

  const BidsPage({super.key, required this.jobId});

  @override
  State<BidsPage> createState() => _BidsPageState();
}

class _BidsPageState extends State<BidsPage> {
  List<dynamic> _bids = [];
  bool _isLoading = true;
  Timer? _timer; // لتحديث العروض كل بضع ثوانٍ

  @override
  void initState() {
    super.initState();
    _fetchBids();
    // تحديث تلقائي كل 5 ثوانٍ لجلب العروض الجديدة
    _timer = Timer.periodic(
      const Duration(seconds: 5),
      (timer) => _fetchBids(),
    );
  }

  @override
  void dispose() {
    _timer?.cancel(); // إيقاف التحديث عند إغلاق الشاشة
    super.dispose();
  }

  // 🚀 جلب العروض من السيرفر (نقطة 5.3 في الدليل)
  Future<void> _fetchBids() async {
    try {
      final response = await ApiClient.get('/jobs/${widget.jobId}/bids');

      if (response.success && response.data != null) {
        setState(() {
          _bids = response.data['data'] as List? ?? [];
          _isLoading = false;
        });
      } else {
        setState(() => _isLoading = false);
      }
    } catch (e) {
      debugPrint("Error fetching bids: $e");
      setState(() => _isLoading = false);
    }
  }

  // ✅ قبول العرض (نقطة 5.3 في الدليل)
  Future<void> _acceptBid(String bidId, String driverName) async {
    setState(() => _isLoading = true);

    try {
      final response = await ApiClient.patch(
        '/jobs/${widget.jobId}/bids/$bidId/accept',
      );

      if (response.success) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text("تم قبول عرض $driverName بنجاح!"),
              backgroundColor: Colors.green,
            ),
          );

          Navigator.pushReplacement(
            context,
            MaterialPageRoute(
              builder: (context) => UserTrackingPage(jobId: widget.jobId),
            ),
          );
        }
      } else {
        _showError(response.message);
      }
    } catch (e) {
      _showError("خطأ في الاتصال: $e");
    } finally {
      if (mounted) setState(() => _isLoading = false);
    }
  }

  void _showError(String msg) {
    ScaffoldMessenger.of(
      context,
    ).showSnackBar(SnackBar(content: Text(msg), backgroundColor: Colors.red));
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: const Text("العروض المتاحة"),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          centerTitle: true,
          actions: [
            IconButton(onPressed: _fetchBids, icon: const Icon(Icons.refresh)),
          ],
        ),
        body: Column(
          children: [
            _buildInfoBanner(),
            Expanded(
              child: _isLoading && _bids.isEmpty
                  ? const Center(child: CircularProgressIndicator())
                  : _bids.isEmpty
                  ? _buildEmptyState()
                  : _buildBidsList(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoBanner() {
    return Container(
      padding: const EdgeInsets.all(15),
      color: Colors.amber[50],
      child: const Row(
        children: [
          Icon(Icons.timer_outlined, color: Colors.orange),
          SizedBox(width: 10),
          Expanded(child: Text("جاري استقبال عروض الكباتن الآن..")),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.local_shipping_outlined, size: 80, color: Colors.grey),
          SizedBox(height: 10),
          Text(
            "في انتظار أول عرض..",
            style: TextStyle(color: Colors.grey, fontSize: 18),
          ),
        ],
      ),
    );
  }

  Widget _buildBidsList() {
    return ListView.builder(
      itemCount: _bids.length,
      itemBuilder: (context, index) {
        final bid = _bids[index];
        // معالجة البيانات من الـ API (تأكد من مسميات الحقول من الدليل)
        final driver = bid['driver'] ?? {};
        final bidId = bid['id']?.toString() ?? '';
        final price = bid['price']?.toString() ?? '0';
        final driverName = driver['name'] ?? "كابتن وصلة";
        final vehicleType = driver['vehicleType'] ?? 'نقل';
        final rating = (driver['ratingAvg'] ?? 5.0).toDouble();
        final totalJobs = driver['totalJobs'] ?? 0;

        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 15, vertical: 8),
          elevation: 3,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(15),
          ),
          child: Padding(
            padding: const EdgeInsets.all(15),
            child: Row(
              children: [
                _buildDriverAvatar(rating.toString()),
                const SizedBox(width: 15),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        driverName,
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      Text(
                        "المركبة: $vehicleType",
                        style: const TextStyle(color: Colors.grey),
                      ),
                      const SizedBox(height: 5),
                      Row(
                        children: [
                          _buildRatingTag(rating),
                          const SizedBox(width: 10),
                          Text(
                            "($totalJobs رحلة)",
                            style: const TextStyle(
                              color: Colors.grey,
                              fontSize: 12,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
                _buildPriceAndAction(bidId, price, driverName),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildDriverAvatar(String rating) {
    return Stack(
      alignment: Alignment.bottomRight,
      children: [
        const CircleAvatar(
          radius: 30,
          backgroundColor: AppColors.primary,
          child: Icon(Icons.person, color: Colors.white, size: 35),
        ),
        CircleAvatar(
          radius: 10,
          backgroundColor: Colors.green,
          child: const Icon(Icons.check, size: 12, color: Colors.white),
        ),
      ],
    );
  }

  Widget _buildRatingTag(double rating) {
    return Row(
      children: [
        const Icon(Icons.star, color: Colors.orange, size: 18),
        Text(" $rating", style: const TextStyle(fontWeight: FontWeight.bold)),
      ],
    );
  }

  Widget _buildPriceAndAction(String bidId, String price, String name) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Text(
          "$price ج.م",
          style: const TextStyle(
            fontWeight: FontWeight.bold,
            color: Colors.green,
            fontSize: 18,
          ),
        ),
        const SizedBox(height: 10),
        ElevatedButton(
          onPressed: () => _acceptBid(bidId, name),
          style: ElevatedButton.styleFrom(
            backgroundColor: AppColors.primary,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(10),
            ),
          ),
          child: const Text("قبول", style: TextStyle(color: Colors.white)),
        ),
      ],
    );
  }
}
