package com.junko.junkodaily.feature.experiment.data.contract

import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun FeatureScreen() {
    SmartPermissionWrapper(
        permission = Manifest.permission.READ_CONTACTS,
        permissionName = "通讯录",
        onPermissionGranted = {
            // 权限通过，直接调用业务逻辑
            doFetchContacts()
        }
    ) { onRequestPermission ->

        // 你的普通 UI
        Button(onClick = { onRequestPermission() }) {
            Text("读取通讯录")
        }
    }
    ContactPermissionRequestScreen({ println("权限通过")})
    RequestMultiplePermissionsButton({ println("全部权限通过")})

    ContentProviderDemoScreen()
}

fun doFetchContacts() {
    // 真正的业务逻辑
    println("执行真正的业务逻辑")
}