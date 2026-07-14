package com.example.learncompose.ui.screen

import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage

@Composable
fun MediaPickerScreen() {
    // 获取当前上下文，MediaPlayer 需要用到
    val context = LocalContext.current

    // --- 状态定义 ---
    var selectedMediaUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAudioUri by remember { mutableStateOf<Uri?>(null) }

    // 播放器实例与播放状态
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    var isVideoSelected by remember { mutableStateOf(false) }

    // 2. 注册照片选择器的 Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            // 当用户选完图片/视频返回时，Uri 会回调到这里（如果取消了则为 null）
            selectedMediaUri = uri
            if (uri != null) {
                val mimeType = context.contentResolver.getType(uri)
                isVideoSelected = mimeType?.startsWith("video/") == true
            }
        }
    )

    // 1. 注册获取文件内容的 Launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            selectedAudioUri = uri
        }
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
            Text("当前选中的图片 Uri: $uri")

            if (isVideoSelected) {
                // 如果是视频，调用我们自定义的视频播放组件
                VideoPlayer(
                    uri = uri,
                    modifier = Modifier
                        .width(300.dp)
                        .height(200.dp)
                )
            } else {
                AsyncImage(
                    model = uri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .height(200.dp),
                    contentScale = ContentScale.FillHeight
                )
            }

        }

        Button(onClick = {
            // 2. 启动并传入指定的 MimeType
            // "audio/*" 表示选择任意音频文件
            // 如果只想选 mp3 可以写 "audio/mpeg"
            audioPickerLauncher.launch("audio/*")
        }) {
            Text("选取系统音乐/音频")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedAudioUri?.let { uri ->
            Text("选中的音乐路径: $uri")

            Button(onClick = {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.pause()
                        isPlaying = false
                    } else {
                        player.start()
                        isPlaying = true
                    }
                }
            }) {
                Text(if (isPlaying) "暂停播放 ⏸️" else "开始播放 ▶️")
            }
        }
    }

    // --- 音频播放器生命周期管理 ---
    // 当 selectedAudioUri 发生变化时，会重新执行这里的代码
    DisposableEffect(selectedAudioUri) {
        if (selectedAudioUri != null) {
            // 创建并准备播放器
            mediaPlayer = MediaPlayer.create(context, selectedAudioUri)
            // 监听播放完成事件，重置按钮状态
            mediaPlayer?.setOnCompletionListener {
                isPlaying = false
            }
        }

        // 当组件被销毁，或者 selectedAudioUri 改变时，释放旧的资源
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        }
    }
}

/**
 * 封装的 ExoPlayer 视频播放组件
 */
@Composable
fun VideoPlayer(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 初始化 ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    LaunchedEffect(uri) {
        exoPlayer.stop() // 先停止上一个视频
        exoPlayer.setMediaItem(MediaItem.fromUri(uri)) // 加载新视频 Uri
        exoPlayer.prepare() // 重新准备
        exoPlayer.playWhenReady = true // 自动播放新视频（如果不希望自动播放，可以设为 false）
    }

    // 生命周期管理：组件被销毁时，释放播放器资源
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // 使用 AndroidView 将传统的原生 PlayerView 嵌入到 Compose 中
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                // useController = true // 默认就是 true，会显示自带的播放/暂停、进度条控件
            }
        },
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MediaPickerScreen()
    
}