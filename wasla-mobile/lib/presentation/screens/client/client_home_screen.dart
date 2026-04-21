import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';
import '../../../data/services/job_service.dart';
import '../../../data/services/auth_service.dart';
import '../../../data/models/job_model.dart';
import '../../../data/models/api_response_model.dart';
import '../../widgets/status_badge.dart';
import '../../widgets/empty_state.dart';
import '../../widgets/custom_snackbar.dart';

class ClientHomeScreen extends StatefulWidget {
  const ClientHomeScreen({super.key});

  @override
  State<ClientHomeScreen> createState() => _ClientHomeScreenState();
}

class _ClientHomeScreenState extends State<ClientHomeScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  final JobService _jobService = JobService();
  final AuthService _authService = AuthService();

  List<JobModel> _activeJobs = [];
  List<JobModel> _completedJobs = [];
  bool _isLoadingActive = false;
  bool _isLoadingCompleted = false;
  int _activePage = 0;
  int _completedPage = 0;
  bool _hasMoreActive = true;
  bool _hasMoreCompleted = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadActiveJobs();
    _loadCompletedJobs();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadActiveJobs({bool refresh = false}) async {
    if (_isLoadingActive) return;

    if (refresh) {
      _activePage = 0;
      _activeJobs.clear();
      _hasMoreActive = true;
    }

    setState(() => _isLoadingActive = true);

    try {
      final PaginatedResponse<JobModel> response = await _jobService.getMyJobs(
        page: _activePage,
        size: 20,
      );

      if (mounted) {
        setState(() {
          final activeStatuses = [
            'OPEN',
            'BIDDING',
            'CONFIRMED',
            'IN_PROGRESS',
          ];
          final filteredJobs = response.content
              .where((job) => activeStatuses.contains(job.status))
              .toList();

          if (refresh) {
            _activeJobs = filteredJobs;
          } else {
            _activeJobs.addAll(filteredJobs);
          }

          _hasMoreActive = !response.last;
          _activePage++;
        });
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoadingActive = false);
      }
    }
  }

  Future<void> _loadCompletedJobs({bool refresh = false}) async {
    if (_isLoadingCompleted) return;

    if (refresh) {
      _completedPage = 0;
      _completedJobs.clear();
      _hasMoreCompleted = true;
    }

    setState(() => _isLoadingCompleted = true);

    try {
      final PaginatedResponse<JobModel> response = await _jobService.getMyJobs(
        page: _completedPage,
        size: 20,
      );

      if (mounted) {
        setState(() {
          final completedStatuses = ['COMPLETED', 'EXPIRED'];
          final filteredJobs = response.content
              .where((job) => completedStatuses.contains(job.status))
              .toList();

          if (refresh) {
            _completedJobs = filteredJobs;
          } else {
            _completedJobs.addAll(filteredJobs);
          }

          _hasMoreCompleted = !response.last;
          _completedPage++;
        });
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoadingCompleted = false);
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
                color: AppColors.primary,
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Icon(
                Icons.local_shipping_rounded,
                size: 20,
                color: Colors.white,
              ),
            ),
            const SizedBox(width: 8),
            const Text('وصلة'),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _logout,
            tooltip: 'تسجيل الخروج',
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(text: 'نشطة'),
            Tab(text: 'مكتملة'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: [
          // Active Jobs Tab
          RefreshIndicator(
            onRefresh: () => _loadActiveJobs(refresh: true),
            child: _activeJobs.isEmpty && !_isLoadingActive
                ? EmptyState(
                    icon: Icons.work_outline,
                    title: 'لا توجد وظائف نشطة',
                    subtitle: 'ابدأ بإنشاء طلب نقل جديد',
                    actionLabel: 'إنشاء طلب',
                    onAction: () {
                      // Navigate to create job
                    },
                  )
                : ListView.builder(
                    itemCount: _activeJobs.length + (_hasMoreActive ? 1 : 0),
                    itemBuilder: (context, index) {
                      if (index == _activeJobs.length) {
                        if (!_isLoadingActive) {
                          _loadActiveJobs();
                        }
                        return const Center(
                          child: Padding(
                            padding: EdgeInsets.all(16),
                            child: CircularProgressIndicator(),
                          ),
                        );
                      }
                      return _JobCard(job: _activeJobs[index]);
                    },
                  ),
          ),

          // Completed Jobs Tab
          RefreshIndicator(
            onRefresh: () => _loadCompletedJobs(refresh: true),
            child: _completedJobs.isEmpty && !_isLoadingCompleted
                ? const EmptyState(
                    icon: Icons.check_circle_outline,
                    title: 'لا توجد وظائف مكتملة',
                    subtitle: 'ستظهر هنا الوظائف المكتملة والمنتهية',
                  )
                : ListView.builder(
                    itemCount:
                        _completedJobs.length + (_hasMoreCompleted ? 1 : 0),
                    itemBuilder: (context, index) {
                      if (index == _completedJobs.length) {
                        if (!_isLoadingCompleted) {
                          _loadCompletedJobs();
                        }
                        return const Center(
                          child: Padding(
                            padding: EdgeInsets.all(16),
                            child: CircularProgressIndicator(),
                          ),
                        );
                      }
                      return _JobCard(job: _completedJobs[index]);
                    },
                  ),
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {
          // Navigate to create job screen
          CustomSnackbar.showInfo(context, 'شاشة إنشاء الوظيفة قيد التطوير');
        },
        icon: const Icon(Icons.add),
        label: const Text('طلب جديد'),
      ),
    );
  }
}

class _JobCard extends StatelessWidget {
  final JobModel job;

  const _JobCard({required this.job});

  @override
  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        onTap: () {
          // Navigate to job details
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
                  StatusBadge(status: job.status),
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
                  const Icon(
                    Icons.location_on_outlined,
                    size: 16,
                    color: AppColors.success,
                  ),
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
                  const Icon(
                    Icons.flag_outlined,
                    size: 16,
                    color: AppColors.error,
                  ),
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
                  const Icon(
                    Icons.inventory_2_outlined,
                    size: 16,
                    color: AppColors.textSecondary,
                  ),
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
                const SizedBox(height: 12),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: AppColors.primary.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    '${job.bidCount} عروض',
                    style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                      color: AppColors.primary,
                    ),
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
