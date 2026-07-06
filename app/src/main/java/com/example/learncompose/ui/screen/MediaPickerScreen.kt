package com.example.learncompose.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MediaPickerScreen() {
    // 1. 定义一个变量用来保存选中的媒体 Uri
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }

    // 2. 注册照片选择器的 Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            // 当用户选完图片/视频返回时，Uri 会回调到这里（如果取消了则为 null）
            selectedMediaUri = uri
        }
    )

    Column {
        // 示例 A：只选择图片
        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }) {
            Text("仅选择图片")
        }

        // 示例 B：只选择视频
        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }) {
            Text("仅选择视频")
        }

        // 示例 C：图片和视频都能选
        Button(onClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
            )
        }) {
            Text("选择图片或视频")
        }

        // 显示结果
        selectedMediaUri?.let { uri ->
            Text("当前选中的 Uri: $uri")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MediaPickerScreen()
    
}