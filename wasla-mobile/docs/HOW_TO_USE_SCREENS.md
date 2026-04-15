# Screen Navigation Guide

All 14 screens are implemented and connected.

## Available Routes

### Auth Screens
- `/` - Splash Screen
- `/role-selection` - Role Selection
- `/login` - Login
- `/register/client` - Client Registration
- `/register/driver` - Driver Registration

### Client Screens
- `/client/home` - Client Home
- `/client/create-job` - Create Job (Map)
- `/client/job-details` - Job Details
- `/client/bids` - Bid List
- `/client/tracking` - Live Tracking
- `/client/rate` - Rate Driver

### Driver Screens
- `/driver/home` - Driver Home
- `/driver/jobs` - Driver Jobs

### Debug
- `/debug/connection-test` - Connection Test

## Navigation Examples

### Client Flow
```dart
// Navigate to create job
Navigator.pushNamed(
  context,
  '/client/create-job',
  arguments: {'userName': 'أحمد محمد'},
);

// Navigate to job details
Navigator.pushNamed(
  context,
  '/client/job-details',
  arguments: {
    'userName': 'أحمد محمد',
    'pickUp': LatLng(30.0444, 31.2357),
    'dropOff': LatLng(30.0500, 31.2400),
    'pickUpAddress': 'مدينة نصر',
    'dropOffAddress': 'التجمع الخامس',
  },
);

// Navigate to bids
Navigator.pushNamed(
  context,
  '/client/bids',
  arguments: {'jobId': 'job_123'},
);

// Navigate to tracking
Navigator.pushNamed(
  context,
  '/client/tracking',
  arguments: {'jobId': 'job_123'},
);

// Navigate to rating
Navigator.pushNamed(
  context,
  '/client/rate',
  arguments: {
    'jobId': 'job_123',
    'driverName': 'محمد السائق',
    'bidPrice': 250.0,
    'distance': 15.5,
  },
);
```

### Driver Flow
```dart
// Navigate to driver home
Navigator.pushReplacementNamed(context, '/driver/home');

// Navigate to driver jobs
Navigator.pushNamed(context, '/driver/jobs');
```

## Screen Status

| Screen | Status | Location |
|--------|--------|----------|
| **Auth Screens (5)** | ✅ Complete | `lib/presentation/screens/auth/` |
| **Client Home** | ✅ Complete | `lib/presentation/screens/client/` |
| **Create Job** | ✅ Complete | `lib/user_app/user_home.dart` |
| **Job Details** | ✅ Complete | `lib/user_app/order_details.dart` |
| **Bid List** | ✅ Complete | `lib/user_app/bids_page.dart` |
| **Live Tracking** | ✅ Complete | `lib/user_app/user_tracking_page.dart` |
| **Rate Driver** | ✅ Complete | `lib/user_app/ride_summary_page.dart` |
| **Driver Home** | ✅ Complete | `lib/presentation/screens/driver/` |
| **Driver Jobs** | ✅ Complete | `lib/driver_app/driver_home.dart` |
| **Connection Test** | ✅ Complete | `lib/presentation/screens/debug/` |

## Complete User Flows

### Client Journey
1. Splash → Role Selection → Login/Register
2. Client Home → Create Job (Map)
3. Job Details → Bid List
4. Accept Bid → Live Tracking
5. Complete → Rate Driver

### Driver Journey
1. Splash → Role Selection → Login/Register
2. Driver Home → Driver Jobs
3. Browse Jobs → Submit Bid
4. Wait for Acceptance → Start Job

## Integration Notes

- Legacy screens (`user_app/`, `driver_app/`) are fully functional
- New screens (`presentation/screens/`) use modern architecture
- All screens connected via `onGenerateRoute` in `main.dart`
- Parameters passed via `arguments` map
