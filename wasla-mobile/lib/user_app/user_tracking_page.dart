import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'dart:async';
import '../core/theme/app_colors.dart';
import '../core/api/api_client.dart';
import 'ride_summary_page.dart';

class UserTrackingPage extends StatefulWidget {
  final String jobId;

  const UserTrackingPage({super.key, required this.jobId});

  @override
  State<UserTrackingPage> createState() => _UserTrackingPageState();
}

class _UserTrackingPageState extends State<UserTrackingPage> {
  final LatLng _driverLocation = const LatLng(30.0444, 31.2357);
  final LatLng _userLocation = const LatLng(30.0600, 31.2500);
  final MapController _mapController = MapController();
  Timer? _locationTimer;
  String _jobStatus = 'IN_PROGRESS';
  String _driverPhone = '';
  final String _driverName = 'الكابتن';
  final String _vehicleType = 'شاحنة';

  @override
  void initState() {
    super.initState();
    _fetchJobDetails();
    // تحديث الموقع كل 5 ثوانٍ (في الإنتاج استخدم WebSocket)
    _locationTimer = Timer.periodic(
      const Duration(seconds: 5),
      (timer) => _fetchJobDetails(),
    );
  }

  @override
  void dispose() {
    _locationTimer?.cancel();
    super.dispose();
  }

  // جلب تفاصيل الوظيفة (نقطة 5.2)
  Future<void> _fetchJobDetails() async {
    try {
      final response = await ApiClient.get('/jobs/${widget.jobId}');

      if (response.success && response.data != null) {
        final job = response.data['data'];
        setState(() {
          _jobStatus = job['status'] ?? 'IN_PROGRESS';
          _driverPhone = job['driverPhone'] ?? '';

          // TODO: في الإنتاج، استخدم WebSocket للموقع الفوري
          // هنا نستخدم موقع ثابت كمثال
        });

        // إذا اكتملت الوظيفة، انتقل لصفحة الملخص
        if (_jobStatus == 'COMPLETED' && mounted) {
          _locationTimer?.cancel();
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(
              builder: (context) => RideSummaryPage(jobId: widget.jobId),
            ),
          );
        }
      }
    } catch (e) {
      debugPrint('Error fetching job details: $e');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: const Text("تتبع شحنتك 🚛"),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          leading: IconButton(
            icon: const Icon(Icons.arrow_back),
            onPressed: () => Navigator.pop(context),
          ),
        ),
        body: Stack(
          children: [
            FlutterMap(
              mapController: _mapController,
              options: MapOptions(
                initialCenter: _driverLocation,
                initialZoom: 14.0,
              ),
              children: [
                TileLayer(
                  urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                  userAgentPackageName: 'com.taher.wasla',
                ),
                PolylineLayer(
                  polylines: [
                    Polyline(
                      points: [_driverLocation, _userLocation],
                      color: AppColors.primary.withValues(alpha: 0.7),
                      strokeWidth: 5.0,
                      isDotted: true,
                    ),
                  ],
                ),
                MarkerLayer(
                  markers: [
                    Marker(
                      point: _userLocation,
                      width: 50,
                      height: 50,
                      child: const Icon(
                        Icons.location_on,
                        color: Colors.blue,
                        size: 40,
                      ),
                    ),
                    Marker(
                      point: _driverLocation,
                      width: 50,
                      height: 50,
                      child: const Icon(
                        Icons.local_shipping,
                        color: AppColors.primary,
                        size: 40,
                      ),
                    ),
                  ],
                ),
              ],
            ),

            Positioned(
              bottom: 20,
              left: 20,
              right: 20,
              child: _buildDriverCard(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDriverCard() {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 10)],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            _jobStatus == 'IN_PROGRESS'
                ? "جاري توصيل طلبك الآن..."
                : "الكابتن في الطريق إليك...",
            style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
          ),
          const Divider(),
          Row(
            children: [
              const CircleAvatar(
                backgroundColor: AppColors.primary,
                child: Icon(Icons.person, color: Colors.white),
              ),
              const SizedBox(width: 15),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      _driverName,
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    Text(
                      _vehicleType,
                      style: const TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                  ],
                ),
              ),
              if (_driverPhone.isNotEmpty)
                CircleAvatar(
                  backgroundColor: Colors.green.withValues(alpha: 0.1),
                  child: IconButton(
                    onPressed: () {
                      // ملاحظة: لفتح تطبيق الهاتف، استخدم url_launcher package
                      // launch('tel:$_driverPhone');
                    },
                    icon: const Icon(Icons.phone, color: Colors.green),
                  ),
                ),
            ],
          ),
        ],
      ),
    );
  }
}
