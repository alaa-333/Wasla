import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:http/http.dart' as http;
import '../core/theme/app_colors.dart';
import '../core/theme/app_styles.dart';
import 'order_details.dart';

class UserHome extends StatefulWidget {
  final String userName;
  const UserHome({super.key, required this.userName});

  @override
  State<UserHome> createState() => _UserHomeState();
}

class _UserHomeState extends State<UserHome> {
  LatLng? _pickUpLocation;
  LatLng? _dropOffLocation;
  final MapController _mapController = MapController();
  final TextEditingController _pickUpController = TextEditingController();
  final TextEditingController _dropOffController = TextEditingController();

  bool _isSelectingPickUp =
      true; // 📍 تحديد هل المستخدم بيختار مكان الاستلام أم التوصيل حالياً

  // --- 🌍 دالة البحث وتحويل النص لإحداثيات ---
  Future<void> _searchPlace(String address, bool isPickUp) async {
    if (address.isEmpty) return;

    _showLoading();

    try {
      final url = Uri.parse(
        'https://nominatim.openstreetmap.org/search?q=$address,Egypt&format=json&limit=1',
      );

      final response = await http.get(url, headers: {'User-Agent': 'WaslaApp'});

      if (!mounted) return;
      Navigator.pop(context);

      if (response.statusCode == 200) {
        final List data = json.decode(response.body);
        if (data.isNotEmpty) {
          double lat = double.parse(data[0]['lat']);
          double lon = double.parse(data[0]['lon']);
          LatLng point = LatLng(lat, lon);

          setState(() {
            if (isPickUp) {
              _pickUpLocation = point;
              _pickUpController.text = data[0]['display_name'].split(',')[0];
            } else {
              _dropOffLocation = point;
              _dropOffController.text = data[0]['display_name'].split(',')[0];
            }
          });
          _mapController.move(point, 15.0);
        } else {
          _showError("لم نجد '$address'. حاول كتابة اسم الحي بشكل أوضح.");
        }
      }
    } catch (e) {
      if (mounted) Navigator.pop(context);
      _showError("تأكد من اتصالك بالإنترنت.");
    }
  }

  // --- 🗺️ دالة تحويل الإحداثيات لاسم مكان (Reverse Geocoding) ---
  Future<void> _getAddressFromLatLng(LatLng point, bool isPickUp) async {
    try {
      final url = Uri.parse(
        'https://nominatim.openstreetmap.org/reverse?lat=${point.latitude}&lon=${point.longitude}&format=json',
      );
      final response = await http.get(url, headers: {'User-Agent': 'WaslaApp'});
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        String name = data['display_name'].split(',')[0] ?? "موقع محدد";
        setState(() {
          if (isPickUp) {
            _pickUpController.text = name;
          } else {
            _dropOffController.text = name;
          }
        });
      }
    } catch (_) {}
  }

  void _showLoading() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const Center(
        child: CircularProgressIndicator(color: AppColors.primary),
      ),
    );
  }

  void _showError(String msg) {
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg)));
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        body: Stack(
          children: [
            FlutterMap(
              mapController: _mapController,
              options: MapOptions(
                initialCenter: const LatLng(30.0444, 31.2357),
                initialZoom: 13.0,
                onTap: (tap, point) {
                  setState(() {
                    if (_isSelectingPickUp) {
                      _pickUpLocation = point;
                      _getAddressFromLatLng(point, true);
                    } else {
                      _dropOffLocation = point;
                      _getAddressFromLatLng(point, false);
                    }
                  });
                },
              ),
              children: [
                TileLayer(
                  urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                  userAgentPackageName: 'com.taher.wasla',
                ),
                MarkerLayer(
                  markers: [
                    if (_pickUpLocation != null)
                      Marker(
                        point: _pickUpLocation!,
                        width: 50,
                        height: 50,
                        child: const Icon(
                          Icons.location_on,
                          color: Colors.green,
                          size: 40,
                        ),
                      ),
                    if (_dropOffLocation != null)
                      Marker(
                        point: _dropOffLocation!,
                        width: 50,
                        height: 50,
                        child: const Icon(
                          Icons.location_on,
                          color: Colors.red,
                          size: 40,
                        ),
                      ),
                  ],
                ),
              ],
            ),

            SafeArea(
              child: Column(
                children: [
                  _buildHeader(),
                  // تم إضافة GestureDetector لمعرفة أي حقل يتم الضغط عليه حالياً
                  GestureDetector(
                    onTap: () => setState(() => _isSelectingPickUp = true),
                    child: _buildSearchBox(
                      _pickUpController,
                      "منين؟ (مكان الاستلام)",
                      true,
                      _isSelectingPickUp,
                    ),
                  ),
                  GestureDetector(
                    onTap: () => setState(() => _isSelectingPickUp = false),
                    child: _buildSearchBox(
                      _dropOffController,
                      "لفين؟ (مكان التوصيل)",
                      false,
                      !_isSelectingPickUp,
                    ),
                  ),
                ],
              ),
            ),

            if (_pickUpLocation != null && _dropOffLocation != null)
              Positioned(
                bottom: 30,
                left: 20,
                right: 20,
                child: _buildConfirmButton(),
              ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(12),
      margin: const EdgeInsets.all(15),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 6)],
      ),
      child: Text(
        "أهلاً يا ${widget.userName}، حدد أماكن النقل 🚛",
        textAlign: TextAlign.center,
        style: const TextStyle(
          fontWeight: FontWeight.bold,
          color: AppColors.primary,
        ),
      ),
    );
  }

  Widget _buildSearchBox(
    TextEditingController controller,
    String hint,
    bool isPickUp,
    bool isActive,
  ) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      margin: const EdgeInsets.symmetric(horizontal: 15, vertical: 5),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(15),
        border: isActive
            ? Border.all(color: AppColors.primary, width: 2)
            : null, // تمييز الحقل النشط
        boxShadow: const [BoxShadow(color: Colors.black12, blurRadius: 8)],
      ),
      child: TextField(
        controller: controller,
        textInputAction: TextInputAction.search,
        onSubmitted: (val) => _searchPlace(val, isPickUp),
        decoration: InputDecoration(
          hintText: hint,
          prefixIcon: Icon(
            isPickUp ? Icons.circle : Icons.location_on,
            color: isPickUp ? Colors.green : Colors.red,
            size: 18,
          ),
          suffixIcon: IconButton(
            icon: const Icon(Icons.search, color: AppColors.primary),
            onPressed: () => _searchPlace(controller.text, isPickUp),
          ),
          border: InputBorder.none,
          contentPadding: const EdgeInsets.symmetric(
            vertical: 15,
            horizontal: 10,
          ),
        ),
      ),
    );
  }

  Widget _buildConfirmButton() {
    return ElevatedButton(
      onPressed: () {
        // التحقق من أن المواقع ليست null
        if (_pickUpLocation != null && _dropOffLocation != null) {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => OrderDetails(
                userName: widget.userName,
                pickUp: _pickUpLocation!,
                dropOff: _dropOffLocation!,
                pickUpAddress: _pickUpController.text,
                dropOffAddress: _dropOffController.text,
              ),
            ),
          );
        }
      },
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primary,
        minimumSize: const Size(double.infinity, 60),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
        elevation: 8,
      ),
      child: const Text(
        "تأكيد الأماكن ومتابعة الطلب ✨",
        style: AppStyles.buttonText,
      ),
    );
  }
}
