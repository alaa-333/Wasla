import 'package:flutter/material.dart';
import '../../core/theme/app_colors.dart';

class StatusBadge extends StatelessWidget {
  final String status;
  final bool isSmall;

  const StatusBadge({super.key, required this.status, this.isSmall = false});

  @override
  Widget build(BuildContext context) {
    final config = _getStatusConfig(status);

    return Container(
      padding: EdgeInsets.symmetric(
        horizontal: isSmall ? 8 : 12,
        vertical: isSmall ? 4 : 6,
      ),
      decoration: BoxDecoration(
        color: config.color.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(
          color: config.color.withValues(alpha: 0.3),
          width: 1,
        ),
      ),
      child: Text(
        config.label,
        style: TextStyle(
          color: config.color,
          fontSize: isSmall ? 11 : 12,
          fontWeight: FontWeight.w600,
        ),
      ),
    );
  }

  _StatusConfig _getStatusConfig(String status) {
    switch (status.toUpperCase()) {
      case 'OPEN':
        return _StatusConfig(color: AppColors.statusOpen, label: 'مفتوحة');
      case 'BIDDING':
        return _StatusConfig(
          color: AppColors.statusBidding,
          label: 'تلقّي عروض',
        );
      case 'CONFIRMED':
        return _StatusConfig(color: AppColors.statusConfirmed, label: 'مؤكدة');
      case 'IN_PROGRESS':
        return _StatusConfig(color: AppColors.statusInProgress, label: 'جارية');
      case 'COMPLETED':
        return _StatusConfig(color: AppColors.statusCompleted, label: 'مكتملة');
      case 'EXPIRED':
        return _StatusConfig(color: AppColors.statusExpired, label: 'منتهية');
      case 'PENDING':
        return _StatusConfig(color: AppColors.bidPending, label: 'في الانتظار');
      case 'ACCEPTED':
        return _StatusConfig(color: AppColors.bidAccepted, label: 'مقبول');
      case 'WITHDRAWN':
        return _StatusConfig(color: AppColors.bidWithdrawn, label: 'ملغي');
      default:
        return _StatusConfig(color: AppColors.textSecondary, label: status);
    }
  }
}

class _StatusConfig {
  final Color color;
  final String label;

  _StatusConfig({required this.color, required this.label});
}
