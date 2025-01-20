package com.hsiharki.current_app

import android.app.Service
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import io.flutter.plugin.common.EventChannel
import android.util.Log

class ForegroundAppService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private var lastPackageName: String? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("ForegroundAppService", "Service created")
        startForegroundService()
        startDetection()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startForegroundService() {
        Log.d("ForegroundAppService", "Foreground service started")
        val notification = NotificationHelper.createNotification(this, "App Detection", "Service is running")
        startForeground(1, notification)
    }

    private fun startDetection() {
        handler.post(object : Runnable {
            override fun run() {
                // Retry until EventSink is initialized
                if (eventSink == null) {
                    Log.d("ForegroundAppService", "EventSink not yet initialized, retrying...")
                    handler.postDelayed(this, 500)
                    return
                }

                val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val currentTime = System.currentTimeMillis()

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    currentTime - 1000 * 5, // Check last 5 seconds
                    currentTime
                )

                if (!stats.isNullOrEmpty()) {
                    val recentApp = stats.maxByOrNull { it.lastTimeUsed }
                    if (recentApp != null && recentApp.packageName != lastPackageName) {
                        lastPackageName = recentApp.packageName
                        Log.d("ForegroundAppService", "Foreground app detected: $lastPackageName")

                        eventSink?.success(lastPackageName)
                        Log.d("ForegroundAppService", "Sent to Flutter: $lastPackageName")
                    }
                }

                handler.postDelayed(this, 1000) // Check every second
            }
        })
    }

    companion object {
        @Volatile
        var eventSink: EventChannel.EventSink? = null
            set(value) {
                synchronized(this) {
                    field = value
                }
            }
    }
}
