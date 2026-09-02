package com.example.learncompose.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ButtonPrimary(
    modifier: Modifier = Modifier,
    isPressOnClick: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable (RowScope.() -> Unit)
) {
    val scale = remember { Animatable(1f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPress by interactionSource.collectIsPressedAsState()

    if (isPressOnClick) {
        var pressTime = 0
        var durationBase = 200L
        val pressListener = {
            onClick()
        }
        LaunchedEffect(isPress) {
            while (isPress) {
                delay(durationBase.coerceIn(1L, Long.MAX_VALUE).milliseconds)
                pressTime++
                durationBase -= pressTime
                pressListener()
            }
        }
    }

    // 核心：用来记录和编排当前正在运行的动画协程
    LaunchedEffect(interactionSource) {
        var animJob: Job? = null

        // 监听最原始的物理交互事件流
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    animJob?.cancel()
                    animJob = launch {
                        // 干净利落地缩小到 0.88f
                        scale.animateTo(0.95f, tween(durationMillis = 80))
                    }
                }

                is PressInteraction.Release -> {

                    // 【情况 B：手指抬起】 核心魔法在这里！
                    val previousPressJob = animJob
                    animJob = launch {
                        // 关键：等待按下的动画“稳稳地执行完”（如果是快点，它会在这里等满80ms）
                        previousPressJob?.join()

                        // 接下来，痛快地触发震撼的 Q 弹大招
                        scale.animateTo(1.05f, spring(stiffness = Spring.StiffnessHigh))
                        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    }
                }

                is PressInteraction.Cancel -> {
                    // 如果手势滑出按钮被取消了，平滑恢复即可
                    animJob?.cancel()
                    animJob = launch { scale.animateTo(1f, spring()) }
                }
            }
        }
    }

    Button(
        modifier = modifier
            .graphicsLayer(
                scaleX = scale.value,
                scaleY = scale.value
            ),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,  // 默认静止时的阴影高度
            pressedElevation = 0.dp,  // 💡 按下时阴影变低，模拟物理世界中按钮被“按下去”的视觉反馈
            hoveredElevation = 10.dp,  // 鼠标悬停时的阴影（针对平板/桌面端）
            focusedElevation = 10.dp
        ),
        enabled = isEnabled,
        onClick = if (isPressOnClick) onClick else rememberDebounce { onClick() }
    ) {
        content()
    }
}

@Composable
fun rememberDebounce(
    onClick: () -> Unit
): () -> Unit {
    // 记录上一次有效点击的时间戳
    var lastClickTime by remember { mutableLongStateOf(0L) }
    // 使用 rememberUpdatedState 确保拿到最新的 onClick 回调，防止闭包捕获旧状态
    val currentOnClick by rememberUpdatedState(onClick)

    return remember {
        {
            val currentTime = System.currentTimeMillis()
            // 如果当前时间距离上次点击时间超过了设置的防抖阈值，则触发点击
            if (currentTime - lastClickTime >= 500L) {
                lastClickTime = currentTime
                currentOnClick()
            }
        }
    }
}