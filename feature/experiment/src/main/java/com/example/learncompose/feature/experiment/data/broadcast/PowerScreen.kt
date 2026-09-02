package com.example.learncompose.feature.experiment.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun PowerStatusScreen(context: Context = LocalContext.current) {
    var isCharging by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_POWER_CONNECTED -> isCharging = true
                    Intent.ACTION_POWER_DISCONNECTED -> isCharging = false
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        // 1. 【核心修复】主动获取当前的充电状态初始值
        // ACTION_BATTERY_CHANGED 是 Sticky Broadcast，直接传入 null 即可读出当前状态
        val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // 2. 【核心修复】兼容 Android 14 (API 34) 及以上的注册安全标志
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }

        // 3. 当页面销毁时注销 Receiver，防止内存泄漏
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                // 防止重复注销引发异常
            }
        }
    }

    // UI 显示
    Text(
        text = if (isCharging) "⚡ 正在充电" else "🔋 未充电（断开）",
        style = MaterialTheme.typography.headlineMedium
    )
}