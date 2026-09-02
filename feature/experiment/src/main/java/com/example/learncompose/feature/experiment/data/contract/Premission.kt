package com.example.learncompose.feature.experiment.data.contract

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun ContactPermissionRequestScreen(
    onPermissionGranted: () -> Unit // 权限授予后的回调
) {
    val context = LocalContext.current

    // 1. 检查初始状态：是否已经拥有权限
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 2. 注册权限申请的 Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            onPermissionGranted()
        } else {
            // 用户拒绝了权限，这里可以提示用户或引导去设置
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasPermission) {
            Text("✅ 已获得通讯录权限")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { onPermissionGranted() }) {
                Text("开始读取通讯录")
            }
        } else {
            Text("❌ 尚未获取通讯录权限")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                // 3. 触发系统权限弹窗
                launcher.launch(Manifest.permission.READ_CONTACTS)
            }) {
                Text("申请通讯录权限")
            }
        }
    }
}