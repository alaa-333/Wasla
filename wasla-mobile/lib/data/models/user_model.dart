/// User Model
class UserModel {
  final String id;
  final String email;
  final String phone;
  final String role; // CLIENT or DRIVER
  final bool newUser;

  UserModel({
    required this.id,
    required this.email,
    required this.phone,
    required this.role,
    required this.newUser,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      role: json['role'] ?? '',
      newUser: json['newUser'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'phone': phone,
      'role': role,
      'newUser': newUser,
    };
  }

  bool get isClient => role == 'CLIENT';
  bool get isDriver => role == 'DRIVER';
}
