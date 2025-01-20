import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'current_app_method_channel.dart';

abstract class CurrentAppPlatform extends PlatformInterface {
  /// Constructs a CurrentAppPlatform.
  CurrentAppPlatform() : super(token: _token);

  static final Object _token = Object();

  static CurrentAppPlatform _instance = MethodChannelCurrentApp();

  /// The default instance of [CurrentAppPlatform] to use.
  ///
  /// Defaults to [MethodChannelCurrentApp].
  static CurrentAppPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [CurrentAppPlatform] when
  /// they register themselves.
  static set instance(CurrentAppPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
