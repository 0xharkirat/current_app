import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'current_app_platform_interface.dart';

/// An implementation of [CurrentAppPlatform] that uses method channels.
class MethodChannelCurrentApp extends CurrentAppPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('current_app');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
