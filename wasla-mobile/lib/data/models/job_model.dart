/// Job Model
class JobModel {
  final String id;
  final String clientId;
  final String? driverId;
  final String pickupAddress;
  final double pickupLat;
  final double pickupLng;
  final String dropoffAddress;
  final double dropoffLat;
  final double dropoffLng;
  final String cargoDesc;
  final String status;
  final double? acceptedPrice;
  final String expiresAt;
  final String? completedAt;
  final String createdAt;
  final int bidCount;
  final String? driverPhone;

  JobModel({
    required this.id,
    required this.clientId,
    this.driverId,
    required this.pickupAddress,
    required this.pickupLat,
    required this.pickupLng,
    required this.dropoffAddress,
    required this.dropoffLat,
    required this.dropoffLng,
    required this.cargoDesc,
    required this.status,
    this.acceptedPrice,
    required this.expiresAt,
    this.completedAt,
    required this.createdAt,
    required this.bidCount,
    this.driverPhone,
  });

  factory JobModel.fromJson(Map<String, dynamic> json) {
    return JobModel(
      id: json['id'] ?? '',
      clientId: json['clientId'] ?? '',
      driverId: json['driverId'],
      pickupAddress: json['pickupAddress'] ?? '',
      pickupLat: (json['pickupLat'] ?? 0).toDouble(),
      pickupLng: (json['pickupLng'] ?? 0).toDouble(),
      dropoffAddress: json['dropoffAddress'] ?? '',
      dropoffLat: (json['dropoffLat'] ?? 0).toDouble(),
      dropoffLng: (json['dropoffLng'] ?? 0).toDouble(),
      cargoDesc: json['cargoDesc'] ?? '',
      status: json['status'] ?? '',
      acceptedPrice: json['acceptedPrice'] != null
          ? (json['acceptedPrice'] as num).toDouble()
          : null,
      expiresAt: json['expiresAt'] ?? '',
      completedAt: json['completedAt'],
      createdAt: json['createdAt'] ?? '',
      bidCount: json['bidCount'] ?? 0,
      driverPhone: json['driverPhone'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'clientId': clientId,
      'driverId': driverId,
      'pickupAddress': pickupAddress,
      'pickupLat': pickupLat,
      'pickupLng': pickupLng,
      'dropoffAddress': dropoffAddress,
      'dropoffLat': dropoffLat,
      'dropoffLng': dropoffLng,
      'cargoDesc': cargoDesc,
      'status': status,
      'acceptedPrice': acceptedPrice,
      'expiresAt': expiresAt,
      'completedAt': completedAt,
      'createdAt': createdAt,
      'bidCount': bidCount,
      'driverPhone': driverPhone,
    };
  }

  // Status helpers
  bool get isOpen => status == 'OPEN';
  bool get isBidding => status == 'BIDDING';
  bool get isConfirmed => status == 'CONFIRMED';
  bool get isInProgress => status == 'IN_PROGRESS';
  bool get isCompleted => status == 'COMPLETED';
  bool get isExpired => status == 'EXPIRED';

  bool get canReceiveBids => isOpen || isBidding;
  bool get hasDriver => driverId != null && driverId!.isNotEmpty;
}
