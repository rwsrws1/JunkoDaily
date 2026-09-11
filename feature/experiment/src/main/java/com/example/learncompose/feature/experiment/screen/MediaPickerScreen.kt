package com.example.learncompose.feature.experiment.screen

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.learncompose.feature.experiment.CustomComposeVideoPlayer
import com.example.learncompose.feature.experiment.features.home.page.char.ChartDemoScreen
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun MediaPickerScreen() {
    val context = LocalContext.current

    var selectedMediaUriString by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedMediaUri = selectedMediaUriString?.let { Uri.parse(it) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }
    var isVideoSelected by remember { mutableStateOf(false) }

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    // --- 音频进度与时长状态 ---
    var audioDuration by remember { mutableLongStateOf(0L) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }

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

    // --- 音频生命周期与初始化 ---
    DisposableEffect(selectedAudioUri) {
        if (selectedAudioUri != null) {
            val player = MediaPlayer.create(context, selectedAudioUri)
            mediaPlayer = player
            audioDuration = player?.duration?.toLong()?.coerceAtLeast(0L) ?: 0L
            currentPosition = 0L

            player?.setOnCompletionListener {
                isAudioPlaying = false
                currentPosition = 0L
            }
        }
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            isAudioPlaying = false
            audioDuration = 0L
            currentPosition = 0L
        }
    }

    // --- 播放中周期性刷新进度 ---
    LaunchedEffect(isAudioPlaying, isUserSeeking) {
        while (isAudioPlaying && !isUserSeeking) {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    currentPosition = player.currentPosition.toLong()
                }
            }
            delay(200) // 每 200ms 更新一次进度
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

            // --- 音乐控制面板 ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 播放/暂停按钮
                Button(
                    onClick = {
                        mediaPlayer?.let { player ->
                            if (player.isPlaying) {
                                player.pause()
                                isAudioPlaying = false
                            } else {
                                player.start()
                                isAudioPlaying = true
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isAudioPlaying) "暂停音频 ⏸️" else "播放音频 ▶️")
                }

                // 进度条 Slider
                val sliderValue = if (isUserSeeking) dragPosition else currentPosition.toFloat()
                Slider(
                    value = sliderValue.coerceIn(0f, audioDuration.toFloat().coerceAtLeast(1f)),
                    valueRange = 0f..audioDuration.toFloat().coerceAtLeast(1f),
                    onValueChange = { newValue ->
                        isUserSeeking = true
                        dragPosition = newValue
                    },
                    onValueChangeFinished = {
                        mediaPlayer?.seekTo(dragPosition.toInt())
                        currentPosition = dragPosition.toLong()
                        isUserSeeking = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 当前时长 / 总时长
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayPosition = if (isUserSeeking) dragPosition.toLong() else currentPosition
                    Text(
                        text = formatTime(displayPosition),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatTime(audioDuration),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        ChartDemoScreen()
    }
}

// 毫秒转 mm:ss 格式
private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MediaPickerScreen()
}