package com.example.learncompose.feature.experiment.data.worker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo

@Composable
fun DownloadScreen(
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val workInfo by viewModel.workInfo.collectAsStateWithLifecycle()
    DownloadContent(
        workInfo = workInfo,
        onStartDownload = { viewModel.startDownload() },
        onCancelDownload = { viewModel.cancelDownload() }
    )
}

@Composable
fun DownloadContent(
    workInfo: WorkInfo?,
    onStartDownload: () -> Unit,
    onCancelDownload: () -> Unit
) {
    val context = LocalContext.current
    val state = workInfo?.state
    val progress = workInfo?.progress?.getInt(DownloadWorker.KEY_PROGRESS, 0) ?: 0
    val resultMsg = workInfo?.outputData?.getString(DownloadWorker.KEY_RESULT)
    val isRunning = state == WorkInfo.State.RUNNING

    // 1. 注册权限申请 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 用户同意权限后，启动下载
            onStartDownload()
        } else {
            Toast.makeText(context, "未授予通知权限，后台下载可能被系统中断", Toast.LENGTH_SHORT).show()
            // 即便拒绝，你也可以选择强制启动，但建议提醒用户
            onStartDownload()
        }
    }

    // 2. 点击按钮时的权限检查逻辑
    val onStartClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                onStartDownload()
            } else {
                // 发起权限请求弹窗
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            // Android 12 及以下不需要动态申请通知权限
            onStartDownload()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "当前任务状态: ${state?.name ?: "未开始"}",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isRunning) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "进度: $progress%")
                }

                if (state == WorkInfo.State.SUCCEEDED && resultMsg != null) {
                    Text(
                        text = resultMsg,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (state == WorkInfo.State.CANCELLED) {
                    Text(
                        text = "任务已被取消",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(
                onClick = onStartClick, // 绑定权限检查点击事件
                enabled = !isRunning
            ) {
                Text("开始任务")
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                onClick = { onCancelDownload() },
                enabled = isRunning,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("取消任务")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    DownloadContent(
        workInfo = null,
        onStartDownload = {},
        onCancelDownload = {}
    )
}