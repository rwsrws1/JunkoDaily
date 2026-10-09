package com.junko.junkodaily.feature.experiment.data.network

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NetworkStatusScreen(context: Context = LocalContext.current) {
    // 1. 实例化 NetworkMonitor（实际项目中推荐通过 Hilt 注入）
    val networkMonitor = remember { NetworkMonitor(context.applicationContext) }

    // 2. 将网络 Flow 转化为 Compose State
    val isOnline by networkMonitor.isOnline.collectAsStateWithLifecycle(initialValue = true)

    // 3. 根据网络状态渲染 UI
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isOnline) Color.Transparent else MaterialTheme.colorScheme.errorContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isOnline) {
                Text(text = "🌐 网络已连接", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(
                    text = "⚠️ 网络已断开，请检查网络设置",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}