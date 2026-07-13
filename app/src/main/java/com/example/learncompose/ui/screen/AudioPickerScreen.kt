package com.example.learncompose.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AudioPickerScreen() {
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }

    // 1. 注册获取文件内容的 Launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedAudioUri = uri
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Button(onClick = {
            // 2. 启动并传入指定的 MimeType
            // "audio/*" 表示选择任意音频文件
            // 如果只想选 mp3 可以写 "audio/mpeg"
            audioPickerLauncher.launch("audio/*") 
        }) {
            Text("选取系统音乐/音频")
        }

        selectedAudioUri?.let { uri ->
            Text("选中的音乐路径: $uri")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    AudioPickerScreen()
}