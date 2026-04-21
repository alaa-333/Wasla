/// Driver Profile Model
class DriverProfileModel {
  final String id;
  final String fullName;
  final String email;
  final String phone;
  final String vehicleType;
  final String licensePlate;
  final bool isAvailable;
  final double? currentLat;
  final double? currentLng;
  final double ratingAvg;
  final int totalJobs;
  final String? fcmToken;

  DriverProfileModel({
    required this.id,
    required this.fullName,
    required this.email,
    required this.phone,
    required this.vehicleType,
    required this.licensePlate,
    required this.isAvailable,
    this.currentLat,
    this.currentLng,
    required this.ratingAvg,
    required this.totalJobs,
    this.fcmToken,
  });

  factory DriverProfileModel.fromJson(Map<String, dynamic> json) {
    return DriverProfileModel(
      id: json['id'] ?? '',
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      vehicleType: json['vehicleType'] ?? '',
      licensePlate: json['licensePlate'] ?? '',
      isAvailable: json['isAvailable'] ?? false,
      currentLat: json['currentLat'] != null
          ? (json['currentLat'] as num).toDouble()
          : null,
      currentLng: json['currentLng'] != null
          ? (json['currentLng'] as num).toDouble()
          : null,
      ratingAvg: (json['ratingAvg'] ?? 0).toDouble(),
      totalJobs: json['totalJobs'] ?? 0,
      fcmToken: json['fcmToken'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'vehicleType': vehicleType,
      'licensePlate': licensePlate,
      'isAvailable': isAvailable,
      'currentLat': currentLat,
      'currentLng': currentLng,
      'ratingAvg': ratingAvg,
      'totalJobs': totalJobs,
      'fcmToken': fcmToken,
    };
  }

  DriverProfileModel copyWith({
    bool? isAvailable,
    double? currentLat,
    double? currentLng,
  }) {
    return DriverProfileModel(
      id: id,
      fullName: fullName,
      email: email,
      phone: phone,
      vehicleType: vehicleType,
      licensePlate: licensePlate,
      isAvailable: isAvailable ?? this.isAvailable,
      currentLat: currentLat ?? this.currentLat,
      currentLng: currentLng ?? this.currentLng,
      ratingAvg: ratingAvg,
      totalJobs: totalJobs,
      fcmToken: fcmToken,
    );
  }
}
