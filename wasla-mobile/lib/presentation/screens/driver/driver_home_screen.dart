import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import '../../../core/theme/app_colors.dart';
import '../../../data/services/job_service.dart';
import '../../../data/services/bid_service.dart';
import '../../../data/services/profile_service.dart';
import '../../../data/services/auth_service.dart';
import '../../../data/models/job_model.dart';
import '../../../data/models/bid_model.dart';
import '../../../data/models/driver_profile_model.dart';
import '../../widgets/status_badge.dart';
import '../../widgets/empty_state.dart';
import '../../widgets/custom_snackbar.dart';

class DriverHomeScreen extends StatefulWidget {
  const DriverHomeScreen({super.key});

  @override
  State<DriverHomeScreen> createState() => _DriverHomeScreenState();
}

class _DriverHomeScreenState extends State<DriverHomeScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final JobService _jobService = JobService();
  final BidService _bidService = BidService();
  final ProfileService _profileService = ProfileService();
  final AuthService _authService = AuthService();

  List<JobModel> _nearbyJobs = [];
  List<BidModel> _myBids = [];
  DriverProfileModel? _driverProfile;
  bool _isLoadingNearby = false;
  bool _isLoadingBids = false;
  bool _isLoadingProfile = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadDriverProfile();
    _loadNearbyJobs();
    _loadMyBids();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadDriverProfile() async {
    try {
      final profile = await _profileService.getDriverProfile();
      if (mounted) {
        setState(() {
          _driverProfile = profile;
          _isLoadingProfile = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isLoadingProfile = false);
        CustomSnackbar.showError(context, e.toString());
      }
    }
  }

  Future<void> _loadNearbyJobs() async {
    if (_isLoadingNearby) return;

    setState(() => _isLoadingNearby = true);

    try {
      // Get current location
      final position = await _getCurrentLocation();

      if (position != null) {
        final jobs = await _jobService.getNearbyJobs(
          lat: position.latitude,
          lng: position.longitude,
          radiusKm: 15,
        );

        if (mounted) {
          setState(() => _nearbyJobs = jobs);
        }
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoadingNearby = false);
      }
    }
  }

  Future<void> _loadMyBids() async {
    if (_isLoadingBids) return;

    setState(() => _isLoadingBids = true);

    try {
      final bids = await _bidService.getMyBids();
      if (mounted) {
        setState(() => _myBids = bids);
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoadingBids = false);
      }
    }
  }

  Future<Position?> _getCurrentLocation() async {
    try {
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        if (mounted) {
          CustomSnackbar.showError(context, 'خدمة الموقع غير مفعلة');
        }
        return null;
      }

      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          if (mounted) {
            CustomSnackbar.showError(context, 'تم رفض إذن الموقع');
          }
          return null;
        }
      }

      if (permission == LocationPermission.deniedForever) {
        if (mounted) {
          CustomSnackbar.showError(context, 'إذن الموقع مرفوض بشكل دائم');
        }
        return null;
      }

      return await Geolocator.getCurrentPosition();
    } catch (e) {
      return null;
    }
  }

  Future<void> _toggleAvailability() async {
    if (_driverProfile == null) return;

    final newStatus = !_driverProfile!.isAvailable;

    // Optimistic update
    setState(() {
      _driverProfile = _driverProfile!.copyWith(isAvailable: newStatus);
    });

    try {
      final updatedProfile = await _profileService.updateDriverStatus(
        isAvailable: newStatus,
      );

      if (mounted) {
        setState(() => _driverProfile = updatedProfile);
        CustomSnackbar.showSuccess(
          context,
          newStatus ? 'أنت الآن متاح' : 'أنت الآن غير متاح',
        );
      }
    } catch (e) {
      // Revert on error
      if (mounted) {
        setState(() {
          _driverProfile = _driverProfile!.copyWith(isAvailable: !newStatus);
        });
        CustomSnackbar.showError(context, e.toString());
      }
    }
  }

  Future<void> _logout() async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('تسجيل الخروج'),
        content: const Text('هل أنت متأكد من تسجيل الخروج؟'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(false),
            child: const Text('إلغاء'),
          ),
          TextButton(
            onPressed: () => Navigator.of(context).pop(true),
            style: TextButton.styleFrom(foregroundColor: AppColors.error),
            child: const Text('تسجيل الخروج'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _authService.logout();
      if (mounted) {
        Navigator.of(
          context,
        ).pushNamedAndRemoveUntil('/role-selection', (route) => false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: AppColors.secondary,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(
                Icons.local_shipping_rounded,
                size: 20,
                color: Colors.white,
              ),
            ),
            const SizedBox(width: 8),
            const Text('وصلة - سائق'),
          ],
        ),
        actions: [
          if (!_isLoadingProfile && _driverProfile != null)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8),
              child: Center(
                child: InkWell(
                  onTap: _toggleAvailability,
                  borderRadius: BorderRadius.circular(20),
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: _driverProfile!.isAvailable
                          ? AppColors.success.withValues(alpha: 0.1)
                          : AppColors.textDisabled.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(
                        color: _driverProfile!.isAvailable
                            ? AppColors.success
                            : AppColors.textDisabled,
                      ),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          _driverProfile!.isAvailable
                              ? Icons.check_circle
                              : Icons.cancel,
                          size: 16,
                          color: _driverProfile!.isAvailable
                              ? AppColors.success
                              : AppColors.textDisabled,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          _driverProfile!.isAvailable ? 'متاح' : 'غير متاح',
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: _driverProfile!.isAvailable
                                ? AppColors.success
                                : AppColors.textDisabled,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
            tooltip: 'تسجيل الخروج',
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(text: 'وظائف قريبة'),
            Tab(text: 'عروضي'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // Nearby Jobs Tab
          RefreshIndicator(
            onRefresh: _loadNearbyJobs,
            child: _nearbyJobs.isEmpty && !_isLoadingNearby
                ? const EmptyState(
                    icon: Icons.location_off_outlined,
                    title: 'لا توجد وظائف قريبة',
                    subtitle: 'ابقَ متاحاً لتلقي الإشعارات',
                  )
                : ListView.builder(
                    itemCount: _nearbyJobs.length,
                    itemBuilder: (context, index) {
                      return _NearbyJobCard(job: _nearbyJobs[index]);
                    },
                  ),
          ),

          // My Bids Tab
          RefreshIndicator(
            onRefresh: _loadMyBids,
            child: _myBids.isEmpty && !_isLoadingBids
                ? const EmptyState(
                    icon: Icons.gavel_outlined,
                    title: 'لم تقدّم أي عروض بعد',
                    subtitle: 'ابحث عن وظائف قريبة وقدّم عروضك',
                  )
                : ListView.builder(
                    itemCount: _myBids.length,
                    itemBuilder: (context, index) {
                      return _MyBidCard(bid: _myBids[index]);
                    },
                  ),
          ),
        ],
      ),
    );
  }
}

class _NearbyJobCard extends StatelessWidget {
  final JobModel job;

  const _NearbyJobCard({required this.job});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: () {
          CustomSnackbar.showInfo(context, 'شاشة تفاصيل الوظيفة قيد التطوير');
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  StatusBadge(status: job.status, isSmall: true),
                  Text(
                    _getTimeAgo(job.createdAt),
                    style: const TextStyle(
                      fontSize: 12,
                      color: AppColors.textSecondary,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  const Icon(Icons.location_on_outlined, size: 16),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      job.pickupAddress,
                      style: const TextStyle(fontSize: 14),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.flag_outlined, size: 16),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      job.dropoffAddress,
                      style: const TextStyle(fontSize: 14),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  const Icon(Icons.inventory_2_outlined, size: 16),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      job.cargoDesc,
                      style: const TextStyle(
                        fontSize: 14,
                        color: AppColors.textSecondary,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
              if (job.bidCount > 0) ...[
                const SizedBox(height: 8),
                Text(
                  '${job.bidCount} عروض',
                  style: const TextStyle(
                    fontSize: 12,
                    color: AppColors.textSecondary,
                  ),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  String _getTimeAgo(String dateStr) {
    try {
      final date = DateTime.parse(dateStr);
      final now = DateTime.now();
      final difference = now.difference(date);

      if (difference.inDays > 0) {
        return 'منذ ${difference.inDays} يوم';
      } else if (difference.inHours > 0) {
        return 'منذ ${difference.inHours} ساعة';
      } else if (difference.inMinutes > 0) {
        return 'منذ ${difference.inMinutes} دقيقة';
      } else {
        return 'الآن';
      }
    } catch (e) {
      return '';
    }
  }
}

class _MyBidCard extends StatelessWidget {
  final BidModel bid;

  const _MyBidCard({required this.bid});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: () {
          CustomSnackbar.showInfo(context, 'شاشة تفاصيل الوظيفة قيد التطوير');
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  StatusBadge(status: bid.status, isSmall: true),
                  Text(
                    _getTimeAgo(bid.createdAt),
                    style: const TextStyle(
                      fontSize: 12,
                      color: AppColors.textSecondary,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  const Icon(
                    Icons.attach_money,
                    size: 20,
                    color: AppColors.success,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'عرضي: ${bid.price.toStringAsFixed(2)} جنيه',
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                      color: AppColors.success,
                    ),
                  ),
                ],
              ),
              if (bid.note != null && bid.note!.isNotEmpty) ...[
                const SizedBox(height: 8),
                Text(
                  bid.note!,
                  style: const TextStyle(
                    fontSize: 14,
                    color: AppColors.textSecondary,
                  ),
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  String _getTimeAgo(String dateStr) {
    try {
      final date = DateTime.parse(dateStr);
      final now = DateTime.now();
      final difference = now.difference(date);

      if (difference.inDays > 0) {
        return 'منذ ${difference.inDays} يوم';
      } else if (difference.inHours > 0) {
        return 'منذ ${difference.inHours} ساعة';
      } else if (difference.inMinutes > 0) {
        return 'منذ ${difference.inMinutes} دقيقة';
      } else {
        return 'الآن';
      }
    } catch (e) {
      return '';
    }
  }
}
