
import 'current_app_platform_interface.dart';

class CurrentApp {
  Future<String?> getPlatformVersion() {
    return CurrentAppPlatform.instance.getPlatformVersion();
  }

  Stream<String?> getForegroundAppStream() {
    return CurrentAppPlatform.instance.getForegroundAppStream();
  }

  Future<void> redirectToUsageAccessSettings() {
    return CurrentAppPlatform.instance.redirectToUsageAccessSettings();
  }
}
