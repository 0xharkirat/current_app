import 'package:flutter_test/flutter_test.dart';
import 'package:current_app/current_app.dart';
import 'package:current_app/current_app_platform_interface.dart';
import 'package:current_app/current_app_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockCurrentAppPlatform
    with MockPlatformInterfaceMixin
    implements CurrentAppPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final CurrentAppPlatform initialPlatform = CurrentAppPlatform.instance;

  test('$MethodChannelCurrentApp is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelCurrentApp>());
  });

  test('getPlatformVersion', () async {
    CurrentApp currentAppPlugin = CurrentApp();
    MockCurrentAppPlatform fakePlatform = MockCurrentAppPlatform();
    CurrentAppPlatform.instance = fakePlatform;

    expect(await currentAppPlugin.getPlatformVersion(), '42');
  });
}
