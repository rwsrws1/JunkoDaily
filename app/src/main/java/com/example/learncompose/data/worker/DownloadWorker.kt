package com.example.learncompose.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.learncompose.MainActivity
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
        private const val PREFS_NAME = "download_sp"
        private const val KEY_SAVED_PROGRESS = "saved_progress"
    }

    private val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override suspend fun doWork(): Result {

        try {
            // Android 14 (API 34) 推荐明确传入 FOREGROUND_SERVICE_TYPE
            setForeground(createForegroundInfo())
        } catch (e: Exception) {
            // 如果用户拒绝了通知权限，setForeground 会抛出 Exception
            // 这里捕获它，允许任务继续在后台尝试运行，不至于导致 Crash
            e.printStackTrace()
        }

        // 1. 读取上次保存的进度（默认为 0）
        val lastProgress = sp.getInt(KEY_SAVED_PROGRESS, 0)
        val startStep = (lastProgress / 10) + 1

        for (i in startStep..10) {
            // 2. 如果任务被系统强行中断/挂起
            if (isStopped) {
                // 返回 Result.retry() 告知 WorkManager 之后自动重新排队执行
                return Result.retry()
            }

            delay(500)
            val currentProgress = i * 5

            sp.edit().putInt(KEY_SAVED_PROGRESS, currentProgress).apply()
            setProgress(workDataOf(KEY_PROGRESS to currentProgress))
        }
        sp.edit().remove(KEY_SAVED_PROGRESS).apply()
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
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setShowBadge(true)
        notificationManager.createNotificationChannel(channel)

        // 1. 创建跳转到 MainActivity 的 Intent
        val intent = Intent(context, MainActivity::class.java).apply {
            // 配合 singleTop，防止生成重复界面
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // 2. 创建 PendingIntent (注意 Android 6.0+ 的 FLAG_IMMUTABLE 标志)
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        val pendingIntent = PendingIntent.getActivity(
            context,
            0, // RequestCode
            intent,
            pendingIntentFlags
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("正在后台下载")
            .setContentText("请稍候，任务进行中...")
            .setSmallIcon(android.R.drawable.stat_sys_download) // 使用系统内置图标测试
            .setContentIntent(pendingIntent)
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