/// Rating Model
class RatingModel {
  final String id;
  final String jobId;
  final int score;
  final String? comment;
  final String createdAt;

  RatingModel({
    required this.id,
    required this.jobId,
    required this.score,
    this.comment,
    required this.createdAt,
  });

  factory RatingModel.fromJson(Map<String, dynamic> json) {
    return RatingModel(
      id: json['id'] ?? '',
      jobId: json['jobId'] ?? '',
      score: json['score'] ?? 0,
      comment: json['comment'],
      createdAt: json['createdAt'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'jobId': jobId,
      'score': score,
      'comment': comment,
      'createdAt': createdAt,
    };
  }
}
