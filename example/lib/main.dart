import 'dart:developer';


import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:current_app/current_app.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _currentAppPlugin = CurrentApp();

  @override
  void initState() {
    super.initState();
    initPlatformState();
    initForegroundApp();
  }
  
  void initForegroundApp(){
    _currentAppPlugin.getForegroundAppStream().listen((appName) {
      log("New app detected on flutter side, ${appName}");
    }, onError: (error) {
      print("Error listening to foreground app stream: $error");
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await _currentAppPlugin.getPlatformVersion() ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  void redirectToUsageAccessSettings() async {
    try {
      await _currentAppPlugin.redirectToUsageAccessSettings();
    }
    on PlatformException catch (e) {
      log("Failed to open app settings: ${e.message}");
    }
  }

  void bringToForeground() async {
    try {
      await _currentAppPlugin.bringToForeground();
    }
    on PlatformException catch (e) {
      log("Failed to bring app to foreground: ${e.message}");
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
          actions: [
            IconButton(onPressed: redirectToUsageAccessSettings, icon: Icon(Icons.settings))
          ],
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
        floatingActionButton: FloatingActionButton(onPressed: bringToForeground, child: Icon(Icons.add),),
      ),
    );
  }
}




