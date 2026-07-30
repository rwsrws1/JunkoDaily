package com.example.learncompose.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class DownloadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_PROGRESS = "key_progress"
        const val KEY_RESULT = "key_result"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "download_channel"
    }

    override suspend fun doWork(): Result {

        try {
            // Android 14 (API 34) 推荐明确传入 FOREGROUND_SERVICE_TYPE
            setForeground(createForegroundInfo())
        } catch (e: Exception) {
            // 如果用户拒绝了通知权限，setForeground 会抛出 Exception
            // 这里捕获它，允许任务继续在后台尝试运行，不至于导致 Crash
            e.printStackTrace()
        }

        // 2. 模拟下载任务
        for (i in 1..10) {
            if (isStopped) return Result.failure()

            delay(500)
            val progress = i * 10
            setProgress(workDataOf(KEY_PROGRESS to progress))
        }

        val outputData = workDataOf(KEY_RESULT to "文件下载成功！")
        return Result.success(outputData)
    }

    // 创建前台服务所需的 Notification
    private fun createForegroundInfo(): ForegroundInfo {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // minSdk 29 >= API 26 (Oreo)，直接创建通知渠道
        val channel = NotificationChannel(
            CHANNEL_ID,
            "文件下载服务",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("正在后台下载")
            .setContentText("请稍候，任务进行中...")
            .setSmallIcon(android.R.drawable.stat_sys_download) // 使用系统内置图标测试
            .setOngoing(true)
            .build()

        // minSdk 29 >= API 29 (Q)，直接传递 foregroundServiceType
        return ForegroundInfo(
            NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }
}