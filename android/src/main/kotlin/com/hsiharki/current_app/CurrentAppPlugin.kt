package com.hsiharki.current_app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** CurrentAppPlugin */
class CurrentAppPlugin : FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {
  private lateinit var methodChannel: MethodChannel
  private lateinit var eventChannel: EventChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext

    // Initialize MethodChannel
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "current_app")
    methodChannel.setMethodCallHandler(this)

    // Initialize EventChannel
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "current_app/events")
    eventChannel.setStreamHandler(this)
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "redirectToUsageAccessSettings" -> {
        redirectToUsageAccessSettings()
        result.success(null)
      }
      "startForegroundService" -> {
        startForegroundAppService()
        result.success(null)
      }
      "stopForegroundService" -> {
        stopForegroundAppService()
        result.success(null)
      }
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
    eventChannel.setStreamHandler(null)
  }

  // EventChannel.StreamHandler Implementation
  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    ForegroundAppService.eventSink = events
    startForegroundAppService()
    Log.d("CurrentAppPlugin", "EventChannel listening")
  }

  override fun onCancel(arguments: Any?) {
    ForegroundAppService.eventSink = null
    stopForegroundAppService()
    Log.d("CurrentAppPlugin", "EventChannel canceled")
  }

  // Start Foreground Service
  private fun startForegroundAppService() {
    val intent = Intent(context, ForegroundAppService::class.java)
    context.startForegroundService(intent)
    Log.d("CurrentAppPlugin", "ForegroundAppService started")
  }

  // Stop Foreground Service
  private fun stopForegroundAppService() {
    val intent = Intent(context, ForegroundAppService::class.java)
    context.stopService(intent)
    Log.d("CurrentAppPlugin", "ForegroundAppService stopped")
  }

  // Redirect for Usage access permissions
  private fun redirectToUsageAccessSettings() {
    try {
      val appPackageName = context.packageName
      val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.data = Uri.parse("package:$appPackageName") // Dynamic package name
      context.startActivity(intent)
      Log.d("CurrentAppPlugin", "Redirecting to usage access settings for $appPackageName")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("CurrentAppPlugin", "Failed to redirect to usage access settings: ${e.message}")
    }
  }
}
