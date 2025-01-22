package com.hsiharki.current_app

import android.content.Context
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

/** CurrentAppPlugin */
class CurrentAppPlugin : FlutterPlugin, MethodChannel.MethodCallHandler, EventChannel.StreamHandler {
  private lateinit var methodChannel: MethodChannel
  private lateinit var eventChannel: EventChannel
  private lateinit var context: Context
  private val handler = Handler(Looper.getMainLooper())
  private var lastPackageName: String? = null
  private var isMonitoring = false

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    context = flutterPluginBinding.applicationContext

    // Initialize MethodChannel
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "current_app")
    methodChannel.setMethodCallHandler(this)

    // Initialize EventChannel
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "current_app/events")
    eventChannel.setStreamHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
    eventChannel.setStreamHandler(null)
  }

  override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "redirectToUsageAccessSettings" -> {
        redirectToUsageAccessSettings()
        result.success(null)
      }
      "bringToForeground" -> {
        bringToForeground()
        result.success(null)
      }
      else -> result.notImplemented()
    }
  }

  override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
    if (!isMonitoring) {
      isMonitoring = true
      startMonitoring(events)
    }
  }

  override fun onCancel(arguments: Any?) {
    isMonitoring = false
    stopMonitoring()
  }

  private fun startMonitoring(eventSink: EventChannel.EventSink?) {
    handler.post(object : Runnable {
      override fun run() {
        if (!isMonitoring) return

        val usageStatsManager =
          context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(
          UsageStatsManager.INTERVAL_DAILY,
          currentTime - 1000 * 5,
          currentTime
        )

        val recentApp = stats?.maxByOrNull { it.lastTimeUsed }
        if (recentApp != null && recentApp.packageName != lastPackageName) {
          lastPackageName = recentApp.packageName
          Log.d("CurrentAppPlugin", "Foreground app detected: $lastPackageName")
          eventSink?.success(lastPackageName)
        }

        handler.postDelayed(this, 1000) // Check every second
      }
    })
  }

  private fun stopMonitoring() {
    handler.removeCallbacksAndMessages(null)
  }

  private fun redirectToUsageAccessSettings() {
    try {
      val appPackageName = context.packageName
      val intent = android.content.Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
      intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
      intent.data = android.net.Uri.parse("package:$appPackageName")
      context.startActivity(intent)
      Log.d("CurrentAppPlugin", "Redirecting to usage access settings for $appPackageName")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("CurrentAppPlugin", "Failed to redirect to usage access settings: ${e.message}")
    }
  }

  private fun bringToForeground() {
    try {
      val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
      intent?.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(intent)
      Log.d("CurrentAppPlugin", "Bringing app to foreground")
    } catch (e: Exception) {
      e.printStackTrace()
      Log.e("CurrentAppPlugin", "Failed to bring app to foreground: ${e.message}")
    }
  }
}
