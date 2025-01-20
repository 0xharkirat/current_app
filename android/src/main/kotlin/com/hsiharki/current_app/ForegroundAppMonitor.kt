package com.hsiharki.current_app

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.flutter.plugin.common.EventChannel

class ForegroundAppMonitor(private val context: Context) {

    private val handler = Handler(Looper.getMainLooper())
    private var eventSink: EventChannel.EventSink? = null
    private var lastPackageName: String? = null

    fun startMonitoring(eventSink: EventChannel.EventSink?) {
        this.eventSink = eventSink
        handler.post(object : Runnable {
            override fun run() {
                val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
                val currentTime = System.currentTimeMillis()

                val stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY,
                    currentTime - 5000, // Check last 5 seconds
                    currentTime
                )

                val recentApp = stats?.maxByOrNull { it.lastTimeUsed }
                if (recentApp != null && recentApp.packageName != lastPackageName) {
                    lastPackageName = recentApp.packageName
                    Log.d("ForegroundAppMonitor", "Foreground app detected: $lastPackageName")
                    eventSink?.success(lastPackageName)
                }

                handler.postDelayed(this, 1000) // Check every second
            }
        })
    }

    fun stopMonitoring() {
        handler.removeCallbacksAndMessages(null)
        eventSink = null
    }
}
