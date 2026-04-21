import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import '../core/theme/app_colors.dart';
import '../core/api/api_client.dart';

class DriverHome extends StatefulWidget {
  const DriverHome({super.key});

  @override
  State<DriverHome> createState() => _DriverHomeState();
}

class _DriverHomeState extends State<DriverHome> {
  bool isOnline = false;
  bool _isLoading = false;
  final LatLng _currentLocation = const LatLng(
    30.0444,
    31.2357,
  ); // القاهرة كمثال
  final MapController _mapController = MapController();

  // 🚀 دالة تحديث حالة التوفر على السيرفر (طبقاً للدليل 5.5)
  Future<void> _toggleAvailability(bool status) async {
    setState(() => _isLoading = true);

    try {
      final response = await ApiClient.patch(
        '/drivers/me/status',
        body: {'isAvailable': status},
      );

      if (response.success) {
        setState(() => isOnline = status);
        _showSnackBar(
          status
              ? "أنت الآن متاح لاستقبال الطلبات 🚛"
              : "تم تسجيل الخروج من الخدمة",
        );

        // تحديث الموقع عند الاتصال
        if (status) {
          _updateLocation();
        }
      } else {
        _showSnackBar("فشل تحديث الحالة: ${response.message}", isError: true);
      }
    } catch (e) {
      _showSnackBar("خطأ في الاتصال بالسيرفر", isError: true);
    } finally {
      setState(() => _isLoading = false);
    }
  }

  // تحديث الموقع (نقطة 5.5)
  Future<void> _updateLocation() async {
    try {
      await ApiClient.put(
        '/drivers/me/location',
        body: {
          'lat': _currentLocation.latitude,
          'lng': _currentLocation.longitude,
        },
      );
    } catch (e) {
      debugPrint('Error updating location: $e');
    }
  }

  void _showSnackBar(String message, {bool isError = false}) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: isError ? Colors.red : Colors.green,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        body: Stack(
          children: [
            // الخريطة
            FlutterMap(
              mapController: _mapController,
              options: MapOptions(
                initialCenter: _currentLocation,
                initialZoom: 15.0,
              ),
              children: [
                TileLayer(
                  urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                  userAgentPackageName: 'com.wasla.app',
                ),
                MarkerLayer(
                  markers: [
                    Marker(
                      point: _currentLocation,
                      width: 50,
                      height: 50,
                      child: Icon(
                        Icons.local_shipping,
                        color: isOnline ? AppColors.primary : Colors.grey,
                        size: 40,
                      ),
                    ),
                  ],
                ),
              ],
            ),

            // زرار التحكم (Online/Offline) مع حالة التحميل
            SafeArea(
              child: Align(
                alignment: Alignment.topCenter,
                child: Padding(
                  padding: const EdgeInsets.only(top: 10),
                  child: _isLoading
                      ? const CircularProgressIndicator()
                      : ActionChip(
                          label: Text(
                            isOnline
                                ? "أنت متصل (متاح)"
                                : "ابدأ العمل (أوفلاين)",
                            style: const TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          padding: const EdgeInsets.symmetric(
                            horizontal: 20,
                            vertical: 10,
                          ),
                          backgroundColor: isOnline
                              ? Colors.green
                              : AppColors.primary,
                          onPressed: () => _toggleAvailability(!isOnline),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(30),
                          ),
                        ),
                ),
              ),
            ),

            // زرار تسليم العفش - يظهر فقط في حالة الـ Online
            if (isOnline)
              Positioned(
                bottom: 30,
                right: 20,
                left: 20,
                child: _buildArrivalButton(),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildArrivalButton() {
    return ElevatedButton.icon(
      onPressed: () {
        // إبلاغ العميل بالوصول
        _showSnackBar("تم إبلاغ العميل بالوصول! ✅");
      },
      icon: const Icon(Icons.check_circle, color: Colors.white),
      label: const Text(
        "تم الوصول وتسليم العفش",
        style: TextStyle(
          color: Colors.white,
          fontSize: 16,
          fontWeight: FontWeight.bold,
        ),
      ),
      style: ElevatedButton.styleFrom(
        backgroundColor: Colors.green,
        padding: const EdgeInsets.symmetric(vertical: 18),
        elevation: 5,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      ),
    );
  }
}
