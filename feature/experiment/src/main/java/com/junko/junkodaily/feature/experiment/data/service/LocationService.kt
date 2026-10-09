package com.junko.junkodaily.feature.experiment.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.junko.junkodaily.feature.experiment.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class LocationService : Service() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private var locationJob: Job? = null

    // 暴露给外部UI的实时位置 Flow
    private val _locationFlow = MutableStateFlow<LocationData?>(null)
    val locationFlow: StateFlow<LocationData?> = _locationFlow.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundService()
            ACTION_STOP -> stopForegroundService()
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "LOCATION_SERVICE_CHANNEL"
        val manager = getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(channelId, "前台定位服务", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("定位服务运行中")
            .setContentText("正在实时更新经纬度...")
            .setSmallIcon(R.drawable.distance_24px)
            .setOngoing(true)
            .build()

        startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)

        startMockLocationUpdates()
    }

    // 模拟高德/FusedLocationProviderClient 持续推送位置更新
    private fun startMockLocationUpdates() {
        locationJob?.cancel() // 确保只有一个 Job 在运行
        locationJob = serviceScope.launch {
            var lat = 31.2304
            var lng = 121.4737
            while (isActive) { // 使用 isActive 确保 Job 被 cancel 时能立刻退出循环
                lat += (Math.random() - 0.5) * 0.001
                lng += (Math.random() - 0.5) * 0.001
                _locationFlow.value = LocationData(lat, lng, System.currentTimeMillis())
                delay(1000)
            }
        }
    }

    private fun stopForegroundService() {
        // 1. 立即取消定位计算的任务
        locationJob?.cancel()
        locationJob = null

        // 2. 将数据重置（可选）
        _locationFlow.value = null

        // 3. 移除前台通知栏 & 尝试停止服务
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}

// 位置数据模型
data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)