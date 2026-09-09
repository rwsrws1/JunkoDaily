package com.example.learncompose.core.common

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode

interface SoundManager {
    fun playClickSound()
    fun playWriteSound()
    fun playEraserSound()
    fun release()
}

class RealSoundManager(context: Context) : SoundManager {
    private val soundPool: SoundPool
    private val clickSound: Int
    private val writeSound: Int
    private val eraserSound: Int

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(1) // 最大同时播放数
            .setAudioAttributes(audioAttributes)
            .build()

        // 加载音效（例如 res/raw/btn_click.wav）
        clickSound = soundPool.load(context, R.raw.click_sound, 1)
        writeSound = soundPool.load(context, R.raw.write_sound, 1)
        eraserSound = soundPool.load(context, R.raw.eraser_sound, 1)
    }

    override fun playClickSound() {
        // 参数：soundID, leftVolume, rightVolume, priority, loop, rate
        soundPool.play(clickSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    override fun playWriteSound() {
        // 参数：soundID, leftVolume, rightVolume, priority, loop, rate
        soundPool.play(writeSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    override fun playEraserSound() {
        // 参数：soundID, leftVolume, rightVolume, priority, loop, rate
        soundPool.play(eraserSound, 1.0f, 1.0f, 1, 0, 1.0f)
    }

    override fun release() {
        soundPool.release()
    }
}

class NoOpSoundManager : SoundManager {
    override fun playClickSound() {}
    override fun playWriteSound() {}
    override fun playEraserSound() {}
    override fun release() {}
}

@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current.applicationContext
    val isPreview = LocalInspectionMode.current
    val soundManager = remember {
        if (isPreview) {
            NoOpSoundManager()
        } else {
            RealSoundManager(context)
        }
    }

    // 当 Composable 销毁时清理资源
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }
    return soundManager
}