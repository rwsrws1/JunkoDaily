package com.junko.junkodaily.feature.experiment.data.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackerScreen(
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current

    // 权限请求启动器
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (locationGranted) {
            val intent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
            }
            context.startForegroundService(intent)
        }
    }

    // 1. 订阅位置数据状态，数据更新时自动触发 UI 重绘
    val location by viewModel.locationState.collectAsStateWithLifecycle()
    val isBound by viewModel.isServiceBound.collectAsStateWithLifecycle()

    // 2. 生命周期绑定：当页面进入时 Bind，退出页面时自动 Unbind
    DisposableEffect(Unit) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // UI 展示面板
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("服务绑定状态: $isBound")
                Text(
                    text = "实时位置信息",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (location != null) {
                    Text(text = "纬度 (Latitude): ${location?.latitude}")
                    Text(text = "经度 (Longitude): ${location?.longitude}")
                    Text(
                        text = "更新时间: ${
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(
                                Date(location?.timestamp ?: 0)
                            )
                        }"
                    )
                } else {
                    Text(text = "暂无位置数据，请先启动服务", color = MaterialTheme.colorScheme.outline)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 控制按钮
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val hasFineLocation = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val hasCoarseLocation = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasFineLocation || hasCoarseLocation) {
                        val intent = Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                        }
                        context.startForegroundService(intent)
                    } else {
                        val permissions = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                }
            ) {
                Text("启动定位")
            }

            OutlinedButton(
                onClick = {
                    val intent = Intent(context, LocationService::class.java).apply {
                        action = LocationService.ACTION_STOP
                    }
                    context.startService(intent)
                }
            ) {
                Text("停止定位")
            }
        }
    }
}