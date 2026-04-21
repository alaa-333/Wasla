import 'driver_info_model.dart';

/// Bid Model
class BidModel {
  final String id;
  final String jobId;
  final double price;
  final String? note;
  final String status;
  final String createdAt;
  final DriverInfoModel driver;

  BidModel({
    required this.id,
    required this.jobId,
    required this.price,
    this.note,
    required this.status,
    required this.createdAt,
    required this.driver,
  });

  factory BidModel.fromJson(Map<String, dynamic> json) {
    return BidModel(
      id: json['id'] ?? '',
      jobId: json['jobId'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      note: json['note'],
      status: json['status'] ?? '',
      createdAt: json['createdAt'] ?? '',
      driver: DriverInfoModel.fromJson(json['driver'] ?? {}),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'jobId': jobId,
      'price': price,
      'note': note,
      'status': status,
      'createdAt': createdAt,
      'driver': driver.toJson(),
    };
  }

  // Status helpers
  bool get isPending => status == 'PENDING';
  bool get isAccepted => status == 'ACCEPTED';
  bool get isWithdrawn => status == 'WITHDRAWN';
}
