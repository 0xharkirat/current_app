
import 'current_app_platform_interface.dart';

class CurrentApp {
  Future<String?> getPlatformVersion() {
    return CurrentAppPlatform.instance.getPlatformVersion();
  }
}
