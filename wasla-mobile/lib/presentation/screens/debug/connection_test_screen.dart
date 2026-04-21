import 'package:flutter/material.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/config/app_config.dart';
import '../../../core/utils/connection_test.dart';

/// شاشة اختبار الاتصال بالـ Backend
class ConnectionTestScreen extends StatefulWidget {
  const ConnectionTestScreen({super.key});

  @override
  State<ConnectionTestScreen> createState() => _ConnectionTestScreenState();
}

class _ConnectionTestScreenState extends State<ConnectionTestScreen> {
  bool _isTesting = false;
  ConnectionTestResult? _result;

  @override
  void initState() {
    super.initState();
    _runTest();
  }

  Future<void> _runTest() async {
    setState(() {
      _isTesting = true;
      _result = null;
    });

    final result = await ConnectionTest.testConnection();

    if (mounted) {
      setState(() {
        _isTesting = false;
        _result = result;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Directionality(
      textDirection: TextDirection.rtl,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('اختبار الاتصال'),
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
          actions: [
            IconButton(
              icon: const Icon(Icons.refresh),
              onPressed: _isTesting ? null : _runTest,
            ),
          ],
        ),
        body: SingleChildScrollView(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Configuration Card
              _buildConfigCard(),
              const SizedBox(height: 16),

              // Test Results
              if (_isTesting)
                const Center(
                  child: Column(
                    children: [
                      CircularProgressIndicator(),
                      SizedBox(height: 16),
                      Text('جاري اختبار الاتصال...'),
                    ],
                  ),
                )
              else if (_result != null)
                _buildResultsCard(),

              const SizedBox(height: 16),

              // Instructions
              _buildInstructionsCard(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildConfigCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.settings, color: AppColors.primary),
                const SizedBox(width: 8),
                Text(
                  'الإعدادات الحالية',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            const Divider(height: 24),
            _buildInfoRow('المنصة', _getPlatformName()),
            _buildInfoRow('Base URL', AppConfig.baseUrl),
            _buildInfoRow('API URL', AppConfig.apiBaseUrl),
            _buildInfoRow('WebSocket', AppConfig.wsUrl),
            _buildInfoRow(
              'وضع الإنتاج',
              AppConfig.baseUrl.contains('railway') ? 'نعم' : 'لا',
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildResultsCard() {
    final result = _result!;
    final isSuccess = result.success;

    return Card(
      color: isSuccess ? Colors.green.shade50 : Colors.red.shade50,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  isSuccess ? Icons.check_circle : Icons.error,
                  color: isSuccess ? Colors.green : Colors.red,
                ),
                const SizedBox(width: 8),
                Text(
                  'نتائج الاختبار',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            const Divider(height: 24),
            ...result.results.entries.map((entry) {
              final icon = entry.value ? '✅' : '❌';
              final status = entry.value ? 'متصل' : 'غير متصل';
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 4),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '$icon ${entry.key}: $status',
                      style: const TextStyle(fontWeight: FontWeight.bold),
                    ),
                    if (!entry.value && result.errors.containsKey(entry.key))
                      Padding(
                        padding: const EdgeInsets.only(right: 24, top: 4),
                        child: Text(
                          'السبب: ${result.errors[entry.key]}',
                          style: TextStyle(
                            color: Colors.red.shade700,
                            fontSize: 12,
                          ),
                        ),
                      ),
                  ],
                ),
              );
            }),
            const Divider(height: 24),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: isSuccess ? Colors.green.shade100 : Colors.red.shade100,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Text(
                result.getSuggestion(),
                style: TextStyle(
                  color: isSuccess
                      ? Colors.green.shade900
                      : Colors.red.shade900,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInstructionsCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Icon(Icons.help_outline, color: AppColors.primary),
                const SizedBox(width: 8),
                Text(
                  'كيفية الإصلاح',
                  style: Theme.of(context).textTheme.titleLarge,
                ),
              ],
            ),
            const Divider(height: 24),
            const Text(
              '1. تأكد من تشغيل Backend:',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: Colors.grey.shade200,
                borderRadius: BorderRadius.circular(4),
              ),
              child: const Text(
                'cd backend-folder\n./mvnw spring-boot:run',
                style: TextStyle(fontFamily: 'monospace', fontSize: 12),
              ),
            ),
            const SizedBox(height: 16),
            const Text(
              '2. تحقق من المنفذ:',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            const Text('Backend يجب أن يعمل على المنفذ 8080'),
            const SizedBox(height: 16),
            const Text(
              '3. للجهاز الفعلي:',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            const Text(
              'افتح lib/core/config/app_config.dart\nوضع IP الخاص بك في _physicalDeviceUrl',
            ),
            const SizedBox(height: 16),
            ElevatedButton.icon(
              onPressed: () {
                Navigator.of(context).pop();
              },
              icon: const Icon(Icons.arrow_back),
              label: const Text('رجوع'),
              style: ElevatedButton.styleFrom(
                backgroundColor: AppColors.primary,
                foregroundColor: Colors.white,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              '$label:',
              style: const TextStyle(
                fontWeight: FontWeight.bold,
                color: Colors.grey,
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
            ),
          ),
        ],
      ),
    );
  }

  String _getPlatformName() {
    return AppConfig.baseUrl.contains('10.0.2.2')
        ? 'Android Emulator'
        : AppConfig.baseUrl.contains('localhost')
        ? 'Web / iOS Simulator'
        : 'Production / Physical Device';
  }
}
