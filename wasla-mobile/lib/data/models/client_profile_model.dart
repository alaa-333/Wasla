/// Client Profile Model
class ClientProfileModel {
  final String id;
  final String fullName;
  final String email;
  final String phone;
  final String? fcmToken;

  ClientProfileModel({
    required this.id,
    required this.fullName,
    required this.email,
    required this.phone,
    this.fcmToken,
  });

  factory ClientProfileModel.fromJson(Map<String, dynamic> json) {
    return ClientProfileModel(
      id: json['id'] ?? '',
      fullName: json['fullName'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      fcmToken: json['fcmToken'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'fullName': fullName,
      'email': email,
      'phone': phone,
      'fcmToken': fcmToken,
    };
  }
}
