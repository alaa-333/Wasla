// ignore: unused_import
import 'package:intl/intl.dart' as intl;
import 'app_localizations.dart';

// ignore_for_file: type=lint

/// The translations for English (`en`).
class AppLocalizationsEn extends AppLocalizations {
  AppLocalizationsEn([String locale = 'en']) : super(locale);

  @override
  String get selectionTitle => 'Who are you?';

  @override
  String get userButton => 'I am a User';

  @override
  String get driverButton => 'I am a Driver';
}
