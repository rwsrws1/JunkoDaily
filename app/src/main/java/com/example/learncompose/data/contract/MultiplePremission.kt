package com.example.learncompose.data.contract

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestMultiplePermissionsButton(onAllGranted: () -> Unit) {
    val context = LocalContext.current

    // 根据 Android 版本适配不同的相册权限
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    // 注册多权限 Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // 判断是否所有权限都被批准了
        val areAllGranted = permissionsMap.values.all { it }
        if (areAllGranted) {
            onAllGranted()
        } else {
            // 处理部分或全部拒绝的情况
        }
    }

    Button(
        onClick = {
            // 检查是否全部已授权，未授权才发起申请
            val allAlreadyGranted = permissionsToRequest.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }

            if (allAlreadyGranted) {
                onAllGranted()
            } else {
                launcher.launch(permissionsToRequest)
            }
        }
    ) {
        Text("获取相册和相机权限")
    }
}