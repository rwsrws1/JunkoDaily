package com.example.learncompose.ui.screen

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.learncompose.ui.components.CustomComposeVideoPlayer

@Composable
fun MediaPickerScreen() {
    val context = LocalContext.current

    // 修改 MediaPickerScreen 中的状态定义
    var selectedMediaUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedMediaUri = selectedMediaUriString?.let { Uri.parse(it) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var isVideoSelected by remember { mutableStateOf(false) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    // --- Launchers ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedMediaUriString = uri?.toString()
            if (uri != null) {
                val mimeType = context.contentResolver.getType(uri)
                isVideoSelected = mimeType?.startsWith("video/") == true
            }
        }
    )

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedAudioUri = uri }
    )

    // --- 音频生命周期 ---
    DisposableEffect(selectedAudioUri) {
        if (selectedAudioUri != null) {
            mediaPlayer = MediaPlayer.create(context, selectedAudioUri)
            mediaPlayer?.setOnCompletionListener { isAudioPlaying = false }
        }
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            isAudioPlaying = false
        }
    }

    // --- UI 布局 ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) { Text("仅选图片") }
            Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }) { Text("仅选视频") }
            Button(onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)) }) { Text("图文视频均可") }
        }

        selectedMediaUri?.let { uri ->
            Text("当前媒体 Uri: $uri")
            if (isVideoSelected) {
                CustomComposeVideoPlayer(uri, modifier = Modifier.height(500.dp))
            } else {
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { audioPickerLauncher.launch("audio/*") }) { Text("选取系统音乐/音频") }

        selectedAudioUri?.let { uri ->
            Text("选中的音乐: $uri")
            Button(onClick = {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.pause()
                        isAudioPlaying = false
                    } else {
                        player.start()
                        isAudioPlaying = true
                    }
                }
            }) {
                Text(if (isAudioPlaying) "暂停音频 ⏸️" else "播放音频 ▶️")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MediaPickerScreen()
}