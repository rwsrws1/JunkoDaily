package com.example.learncompose.ui.components

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.TextureView
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// 【修改点 1: 移除无用的 graphicsLayer 引用】
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/**
 * 【修改点 2: 新增辅助函数，用于从 Context 中获取 Activity】
 */
fun Context.getActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}

/**
 * isSupportLandscape = true 需要添加
 * android:configChanges="keyboard|keyboardHidden|orientation|screenSize|screenLayout|uiMode"
 */
@SuppressLint("SourceLockedOrientationActivity")
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun CustomComposeVideoPlayer(uri: Uri, modifier: Modifier = Modifier, isSupportLandscape: Boolean = false) {
    val context = LocalContext.current
    // 【修改点 3: 获取并记住当前 Activity】
    val activity = remember { context.getActivity() }

    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var videoDuration by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }

    // 全屏状态
    var isSimulatedFullscreen by rememberSaveable { mutableStateOf(false) }
    var videoAspectRatio by remember { mutableFloatStateOf(16f / 9f) }

    // 播放器生命周期与状态同步 (保持原样)
    LaunchedEffect(uri) {
        exoPlayer.setMediaItem(MediaItem.fromUri(uri))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        exoPlayer.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                isPlaying = player.isPlaying
                videoDuration = player.duration.coerceAtLeast(0L)
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    val isPortrait =
                        videoSize.unappliedRotationDegrees == 90 || videoSize.unappliedRotationDegrees == 270
                    val w = if (isPortrait) videoSize.height else videoSize.width
                    val h = if (isPortrait) videoSize.width else videoSize.height
                    videoAspectRatio = w.toFloat() / h.toFloat()
                }
            }
        })
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = exoPlayer.currentPosition
            delay(500)
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    /**
     * 【修改点 4: 处理屏幕方向请求的核心逻辑】
     * 这是一个副作用。当全屏状态改变时，请求 Activity 旋转。
     */
    LaunchedEffect(isSimulatedFullscreen) {
        if (activity == null) return@LaunchedEffect

        if (isSimulatedFullscreen) {
            // 进入全屏：如果是横屏视频 -> 请求系统旋转到横屏
            if (videoAspectRatio > 1f && isSupportLandscape) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                // 如果是竖屏视频 -> 请求系统保持在竖屏
                // 这里显式设置竖屏，防止用户上一个全屏是横屏导致这里也是横向的。
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } else {
            // 退出全屏：请求系统回到默认状态 (通常由 Manifest 定义，或根据需要设置为 PORTRAIT)
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // playerContent 保持原样，没有任何旋转逻辑
    val playerContent = remember {
        androidx.compose.runtime.movableContentOf {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 1. 底层画面
                AndroidView(
                    factory = { ctx ->
                        TextureView(ctx).apply {
                            exoPlayer.setVideoTextureView(this)
                            // 确保画面填充
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.aspectRatio(videoAspectRatio)
                )

                // 2. 上层控制条 (保持原样)
                if (showControls) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isPlaying) "⏸" else "▶️",
                            fontSize = 48.sp,
                            color = Color.White,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clickable {
                                    if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                                }
                        )

                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)

                            Slider(
                                value = currentPosition.toFloat(),
                                valueRange = 0f..(videoDuration.toFloat().coerceAtLeast(1f)),
                                onValueChange = { currentPosition = it.toLong() },
                                onValueChangeFinished = { exoPlayer.seekTo(currentPosition) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )

                            Text(formatTime(videoDuration), color = Color.White, fontSize = 12.sp)

                            Text(
                                text = if (isSimulatedFullscreen) "缩小 ↘️" else "全屏 ↗️",
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .clickable { isSimulatedFullscreen = !isSimulatedFullscreen }
                            )
                        }
                    }
                }
            }
        }

    }

    if (isSimulatedFullscreen) {
        Dialog(
            onDismissRequest = { isSimulatedFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
                // 应用上一个问题的修复方案
                dismissOnBackPress = true
            )
        ) {
            // 移除上一个问题的临时 BackHandler 方案，因为我们正确处理了 dismissOnBackPress=true

            val dialogView = LocalView.current
            val dialogWindow = (dialogView.parent as? DialogWindowProvider)?.window

            LaunchedEffect(dialogWindow) {
                dialogWindow?.let { window ->
                    val controller = WindowCompat.getInsetsController(window, dialogView)
                    controller.hide(WindowInsetsCompat.Type.systemBars())
                    controller.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }

            /**
             * 【修改点 5: 彻底简化全屏布局】
             * 不再需要 BoxWithConstraints，不再需要判断旋转。
             * 因为 Activity 已经被请求旋转了，这里只需要简单的 fillMaxSize()。
             */
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showControls = !showControls }
            ) {
                // 直接放置 playerContent，系统旋转会自动处理一切
                playerContent()
            }
        }
    } else {
        Box(
            modifier = modifier
                .background(Color.Black)
                .clickable { showControls = !showControls }
        ) {
            playerContent()
        }
    }
}

private fun formatTime(timeMs: Long): String {
    if (timeMs < 0) return "00:00"
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMs)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60
    return String.format("%02d:%02d", minutes, seconds)
}