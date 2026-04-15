/// Driver Info Model (embedded in Bid)
class DriverInfoModel {
  final String id;
  final String name;
  final String vehicleType;
  final double ratingAvg;
  final int totalJobs;

  DriverInfoModel({
    required this.id,
    required this.name,
    required this.vehicleType,
    required this.ratingAvg,
    required this.totalJobs,
  });

  factory DriverInfoModel.fromJson(Map<String, dynamic> json) {
    return DriverInfoModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      vehicleType: json['vehicleType'] ?? '',
      ratingAvg: (json['ratingAvg'] ?? 0).toDouble(),
      totalJobs: json['totalJobs'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'vehicleType': vehicleType,
      'ratingAvg': ratingAvg,
      'totalJobs': totalJobs,
    };
  }
}
