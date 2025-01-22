import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'current_app_platform_interface.dart';

/// An implementation of [CurrentAppPlatform] that uses method channels.
class MethodChannelCurrentApp extends CurrentAppPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('current_app');

  /// The event channel used to listen to the foreground app updates.
  @visibleForTesting
  final eventChannel = const EventChannel('current_app/events');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<void> redirectToUsageAccessSettings() async {
    await methodChannel.invokeMethod<String>('redirectToUsageAccessSettings');
  }

  @override
  Stream<String?> getForegroundAppStream() {
    return eventChannel.receiveBroadcastStream().map((event) => event as String?);
  }

  @override
  Future<void> bringToForeground() {
    // TODO: implement bringToForeground
    return methodChannel.invokeMethod('bringToForeground');
  }

}
