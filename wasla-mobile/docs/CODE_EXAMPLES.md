# 💻 أمثلة الكود - Code Examples

مجموعة من الأمثلة العملية لاستخدام المكونات المختلفة في المشروع.

---

## 📚 جدول المحتويات

1. [استدعاء API](#استدعاء-api)
2. [إدارة الحالة](#إدارة-الحالة)
3. [معالجة الأخطاء](#معالجة-الأخطاء)
4. [Widgets المشتركة](#widgets-المشتركة)
5. [Navigation](#navigation)
6. [Forms & Validation](#forms--validation)

---

## 🌐 استدعاء API

### مثال 1: تسجيل الدخول

```dart
import 'package:flutter/material.dart';
import '../data/services/auth_service.dart';
import '../presentation/widgets/custom_snackbar.dart';

class LoginExample extends StatefulWidget {
  const LoginExample({super.key});

  @override
  State<LoginExample> createState() => _LoginExampleState();
}

class _LoginExampleState extends State<LoginExample> {
  final AuthService _authService = AuthService();
  final _emailController = TextEditingController();
  final _passwordController = TextEditingController();
  bool _isLoading = false;

  Future<void> _login() async {
    setState(() => _isLoading = true);

    try {
      final authResponse = await _authService.login(
        email: _emailController.text.trim(),
        password: _passwordController.text,
      );

      if (!mounted) return;

      // Navigate based on role
      if (authResponse.user.isClient) {
        Navigator.of(context).pushReplacementNamed('/client/home');
      } else {
        Navigator.of(context).pushReplacementNamed('/driver/home');
      }
    } catch (e) {
      if (!mounted) return;
      CustomSnackbar.showError(context, e.toString());
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          TextField(controller: _emailController),
          TextField(controller: _passwordController),
          ElevatedButton(
            onPressed: _isLoading ? null : _login,
            child: const Text('تسجيل الدخول'),
          ),
        ],
      ),
    );
  }
}
```

### مثال 2: جلب قائمة مع Pagination

```dart
class JobsListExample extends StatefulWidget {
  const JobsListExample({super.key});

  @override
  State<JobsListExample> createState() => _JobsListExampleState();
}

class _JobsListExampleState extends State<JobsListExample> {
  final JobService _jobService = JobService();
  final ScrollController _scrollController = ScrollController();
  
  List<JobModel> _jobs = [];
  int _currentPage = 0;
  bool _isLoading = false;
  bool _hasMore = true;

  @override
  void initState() {
    super.initState();
    _loadJobs();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels ==
        _scrollController.position.maxScrollExtent) {
      if (!_isLoading && _hasMore) {
        _loadJobs();
      }
    }
  }

  Future<void> _loadJobs({bool refresh = false}) async {
    if (_isLoading) return;

    if (refresh) {
      _currentPage = 0;
      _jobs.clear();
      _hasMore = true;
    }

    setState(() => _isLoading = true);

    try {
      final response = await _jobService.getMyJobs(
        page: _currentPage,
        size: 20,
      );

      if (mounted) {
        setState(() {
          _jobs.addAll(response.content);
          _hasMore = !response.last;
          _currentPage++;
        });
      }
    } catch (e) {
      if (mounted) {
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return RefreshIndicator(
      onRefresh: () => _loadJobs(refresh: true),
      child: ListView.builder(
        controller: _scrollController,
        itemCount: _jobs.length + (_hasMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index == _jobs.length) {
            return const Center(
              child: CircularProgressIndicator(),
            );
          }
          return ListTile(
            title: Text(_jobs[index].pickupAddress),
          );
        },
      ),
    );
  }
}
```

---

## 🔄 إدارة الحالة

### مثال 1: Toggle Switch

```dart
class AvailabilityToggleExample extends StatefulWidget {
  const AvailabilityToggleExample({super.key});

  @override
  State<AvailabilityToggleExample> createState() =>
      _AvailabilityToggleExampleState();
}

class _AvailabilityToggleExampleState
    extends State<AvailabilityToggleExample> {
  final ProfileService _profileService = ProfileService();
  bool _isAvailable = false;
  bool _isUpdating = false;

  Future<void> _toggleAvailability() async {
    final newStatus = !_isAvailable;

    // Optimistic update
    setState(() {
      _isAvailable = newStatus;
      _isUpdating = true;
    });

    try {
      await _profileService.updateDriverStatus(
        isAvailable: newStatus,
      );

      if (mounted) {
        CustomSnackbar.showSuccess(
          context,
          newStatus ? 'أنت الآن متاح' : 'أنت الآن غير متاح',
        );
      }
    } catch (e) {
      // Revert on error
      if (mounted) {
        setState(() => _isAvailable = !newStatus);
        CustomSnackbar.showError(context, e.toString());
      }
    } finally {
      if (mounted) {
        setState(() => _isUpdating = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Switch(
      value: _isAvailable,
      onChanged: _isUpdating ? null : (_) => _toggleAvailability(),
    );
  }
}
```

### مثال 2: Form State

```dart
class FormStateExample extends StatefulWidget {
  const FormStateExample({super.key});

  @override
  State<FormStateExample> createState() => _FormStateExampleState();
}

class _FormStateExampleState extends State<FormStateExample> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  
  bool _isLoading = false;
  Map<String, String> _fieldErrors = {};

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    // Clear previous errors
    setState(() => _fieldErrors.clear());

    if (!_formKey.currentState!.validate()) return;

    setState(() => _isLoading = true);

    try {
      // API call here
      await Future.delayed(const Duration(seconds: 2));

      if (mounted) {
        CustomSnackbar.showSuccess(context, 'تم الحفظ بنجاح');
        Navigator.of(context).pop();
      }
    } on ApiException catch (e) {
      if (mounted) {
        if (e.fieldErrors != null) {
          setState(() => _fieldErrors = e.fieldErrors!);
        }
        CustomSnackbar.showError(context, e.message);
      }
    } finally {
      if (mounted) {
        setState(() => _isLoading = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        children: [
          TextFormField(
            controller: _nameController,
            decoration: InputDecoration(
              labelText: 'الاسم',
              errorText: _fieldErrors['name'],
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'يرجى إدخال الاسم';
              }
              return null;
            },
          ),
          TextFormField(
            controller: _emailController,
            decoration: InputDecoration(
              labelText: 'البريد الإلكتروني',
              errorText: _fieldErrors['email'],
            ),
            validator: (value) {
              if (value == null || value.isEmpty) {
                return 'يرجى إدخال البريد الإلكتروني';
              }
              if (!value.contains('@')) {
                return 'بريد إلكتروني غير صحيح';
              }
              return null;
            },
          ),
          ElevatedButton(
            onPressed: _isLoading ? null : _submit,
            child: _isLoading
                ? const CircularProgressIndicator()
                : const Text('حفظ'),
          ),
        ],
      ),
    );
  }
}
```

---

## ⚠️ معالجة الأخطاء

### مثال 1: معالجة أخطاء API

```dart
Future<void> _loadData() async {
  try {
    final data = await _service.getData();
    setState(() => _data = data);
  } on ApiException catch (e) {
    // معالجة أخطاء API المخصصة
    if (e.statusCode == 401) {
      // Unauthorized - redirect to login
      Navigator.of(context).pushReplacementNamed('/login');
    } else if (e.statusCode == 404) {
      // Not found
      CustomSnackbar.showError(context, 'البيانات غير موجودة');
    } else if (e.fieldErrors != null) {
      // Validation errors
      e.fieldErrors!.forEach((field, message) {
        print('$field: $message');
      });
    } else {
      // Generic error
      CustomSnackbar.showError(context, e.message);
    }
  } on DioException catch (e) {
    // معالجة أخطاء الشبكة
    if (e.type == DioExceptionType.connectionTimeout) {
      CustomSnackbar.showError(context, 'انتهت مهلة الاتصال');
    } else if (e.type == DioExceptionType.connectionError) {
      CustomSnackbar.showError(context, 'لا يوجد اتصال بالإنترنت');
    } else {
      CustomSnackbar.showError(context, 'حدث خطأ في الاتصال');
    }
  } catch (e) {
    // معالجة أخطاء عامة
    CustomSnackbar.showError(context, 'حدث خطأ غير متوقع');
  }
}
```

### مثال 2: Retry Mechanism

```dart
class RetryExample extends StatefulWidget {
  const RetryExample({super.key});

  @override
  State<RetryExample> createState() => _RetryExampleState();
}

class _RetryExampleState extends State<RetryExample> {
  final JobService _jobService = JobService();
  List<JobModel>? _jobs;
  String? _error;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadJobs();
  }

  Future<void> _loadJobs({int retries = 3}) async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    for (int i = 0; i < retries; i++) {
      try {
        final response = await _jobService.getMyJobs();
        if (mounted) {
          setState(() {
            _jobs = response.content;
            _isLoading = false;
          });
        }
        return; // Success - exit
      } catch (e) {
        if (i == retries - 1) {
          // Last retry failed
          if (mounted) {
            setState(() {
              _error = e.toString();
              _isLoading = false;
            });
          }
        } else {
          // Wait before retry
          await Future.delayed(Duration(seconds: i + 1));
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, size: 64),
            const SizedBox(height: 16),
            Text(_error!),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadJobs,
              child: const Text('إعادة المحاولة'),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      itemCount: _jobs?.length ?? 0,
      itemBuilder: (context, index) {
        return ListTile(
          title: Text(_jobs![index].pickupAddress),
        );
      },
    );
  }
}
```

---

## 🎨 Widgets المشتركة

### مثال 1: استخدام StatusBadge

```dart
import '../widgets/status_badge.dart';

class StatusBadgeExample extends StatelessWidget {
  const StatusBadgeExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        StatusBadge(status: 'OPEN'),
        StatusBadge(status: 'BIDDING'),
        StatusBadge(status: 'CONFIRMED'),
        StatusBadge(status: 'IN_PROGRESS'),
        StatusBadge(status: 'COMPLETED'),
        StatusBadge(status: 'EXPIRED'),
        
        // Small version
        StatusBadge(status: 'OPEN', isSmall: true),
      ],
    );
  }
}
```

### مثال 2: استخدام LoadingOverlay

```dart
import '../widgets/loading_overlay.dart';

class LoadingOverlayExample extends StatefulWidget {
  const LoadingOverlayExample({super.key});

  @override
  State<LoadingOverlayExample> createState() => _LoadingOverlayExampleState();
}

class _LoadingOverlayExampleState extends State<LoadingOverlayExample> {
  bool _isLoading = false;

  Future<void> _doSomething() async {
    setState(() => _isLoading = true);
    await Future.delayed(const Duration(seconds: 2));
    setState(() => _isLoading = false);
  }

  @override
  Widget build(BuildContext context) {
    return LoadingOverlay(
      isLoading: _isLoading,
      message: 'جاري التحميل...',
      child: Scaffold(
        body: Center(
          child: ElevatedButton(
            onPressed: _doSomething,
            child: const Text('ابدأ'),
          ),
        ),
      ),
    );
  }
}
```

### مثال 3: استخدام CustomSnackbar

```dart
import '../widgets/custom_snackbar.dart';

class SnackbarExample extends StatelessWidget {
  const SnackbarExample({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        ElevatedButton(
          onPressed: () {
            CustomSnackbar.showSuccess(context, 'تم بنجاح!');
          },
          child: const Text('Success'),
        ),
        ElevatedButton(
          onPressed: () {
            CustomSnackbar.showError(context, 'حدث خطأ!');
          },
          child: const Text('Error'),
        ),
        ElevatedButton(
          onPressed: () {
            CustomSnackbar.showInfo(context, 'معلومة مفيدة');
          },
          child: const Text('Info'),
        ),
      ],
    );
  }
}
```

### مثال 4: استخدام EmptyState

```dart
import '../widgets/empty_state.dart';

class EmptyStateExample extends StatelessWidget {
  const EmptyStateExample({super.key});

  @override
  Widget build(BuildContext context) {
    return EmptyState(
      icon: Icons.work_outline,
      title: 'لا توجد وظائف',
      subtitle: 'ابدأ بإنشاء وظيفة جديدة',
      actionLabel: 'إنشاء وظيفة',
      onAction: () {
        Navigator.of(context).pushNamed('/create-job');
      },
    );
  }
}
```

---

## 🧭 Navigation

### مثال 1: Navigation بسيط

```dart
// Push
Navigator.of(context).pushNamed('/job-details');

// Push with arguments
Navigator.of(context).pushNamed(
  '/job-details',
  arguments: jobId,
);

// Pop
Navigator.of(context).pop();

// Pop with result
Navigator.of(context).pop(result);

// Replace
Navigator.of(context).pushReplacementNamed('/home');

// Clear stack and push
Navigator.of(context).pushNamedAndRemoveUntil(
  '/home',
  (route) => false,
);
```

### مثال 2: Navigation مع Confirmation

```dart
Future<void> _logout() async {
  final confirmed = await showDialog<bool>(
    context: context,
    builder: (context) => AlertDialog(
      title: const Text('تسجيل الخروج'),
      content: const Text('هل أنت متأكد؟'),
      actions: [
        TextButton(
          onPressed: () => Navigator.of(context).pop(false),
          child: const Text('إلغاء'),
        ),
        TextButton(
          onPressed: () => Navigator.of(context).pop(true),
          child: const Text('تأكيد'),
        ),
      ],
    ),
  );

  if (confirmed == true) {
    await _authService.logout();
    if (mounted) {
      Navigator.of(context).pushNamedAndRemoveUntil(
        '/login',
        (route) => false,
      );
    }
  }
}
```

---

## 📝 Forms & Validation

### مثال 1: Form كامل

```dart
class CompleteFormExample extends StatefulWidget {
  const CompleteFormExample({super.key});

  @override
  State<CompleteFormExample> createState() => _CompleteFormExampleState();
}

class _CompleteFormExampleState extends State<CompleteFormExample> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  final _passwordController = TextEditingController();
  
  bool _obscurePassword = true;

  @override
  void dispose() {
    _nameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  String? _validateName(String? value) {
    if (value == null || value.isEmpty) {
      return 'يرجى إدخال الاسم';
    }
    if (value.length < 3) {
      return 'الاسم يجب أن يكون 3 أحرف على الأقل';
    }
    return null;
  }

  String? _validateEmail(String? value) {
    if (value == null || value.isEmpty) {
      return 'يرجى إدخال البريد الإلكتروني';
    }
    if (!value.contains('@')) {
      return 'بريد إلكتروني غير صحيح';
    }
    return null;
  }

  String? _validatePhone(String? value) {
    if (value == null || value.isEmpty) {
      return 'يرجى إدخال رقم الهاتف';
    }
    if (!value.startsWith('+')) {
      return 'يجب أن يبدأ برمز الدولة (+20)';
    }
    if (value.length < 12) {
      return 'رقم هاتف غير صحيح';
    }
    return null;
  }

  String? _validatePassword(String? value) {
    if (value == null || value.isEmpty) {
      return 'يرجى إدخال كلمة المرور';
    }
    if (value.length < 8) {
      return 'كلمة المرور يجب أن تكون 8 أحرف على الأقل';
    }
    return null;
  }

  void _submit() {
    if (_formKey.currentState!.validate()) {
      // Form is valid - proceed
      print('Name: ${_nameController.text}');
      print('Email: ${_emailController.text}');
      print('Phone: ${_phoneController.text}');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('نموذج كامل')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: 'الاسم الكامل',
                prefixIcon: Icon(Icons.person_outline),
              ),
              validator: _validateName,
            ),
            const SizedBox(height: 16),
            
            TextFormField(
              controller: _emailController,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                labelText: 'البريد الإلكتروني',
                prefixIcon: Icon(Icons.email_outlined),
              ),
              validator: _validateEmail,
            ),
            const SizedBox(height: 16),
            
            TextFormField(
              controller: _phoneController,
              keyboardType: TextInputType.phone,
              decoration: const InputDecoration(
                labelText: 'رقم الهاتف',
                prefixIcon: Icon(Icons.phone_outlined),
                hintText: '+201234567890',
              ),
              validator: _validatePhone,
            ),
            const SizedBox(height: 16),
            
            TextFormField(
              controller: _passwordController,
              obscureText: _obscurePassword,
              decoration: InputDecoration(
                labelText: 'كلمة المرور',
                prefixIcon: const Icon(Icons.lock_outlined),
                suffixIcon: IconButton(
                  icon: Icon(
                    _obscurePassword
                        ? Icons.visibility_outlined
                        : Icons.visibility_off_outlined,
                  ),
                  onPressed: () {
                    setState(() => _obscurePassword = !_obscurePassword);
                  },
                ),
              ),
              validator: _validatePassword,
            ),
            const SizedBox(height: 32),
            
            ElevatedButton(
              onPressed: _submit,
              child: const Text('إرسال'),
            ),
          ],
        ),
      ),
    );
  }
}
```

---

**آخر تحديث:** 2026  
**الإصدار:** 1.0.0
