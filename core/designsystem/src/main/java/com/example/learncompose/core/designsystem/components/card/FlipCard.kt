package com.example.learncompose.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

val FloatAnimatableSaver = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { it.value }, // 保存时，只提取当前的 Float 值
    restore = { Animatable(it) } // 恢复时，用保存的 Float 值重新创建 Animatable
)

@Composable
fun FlipCard(
    aspectRatio: Float = 2f/3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isFlip by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 动画状态：旋转角度 (0f ~ 180f) 与 缩放比例 (1f -> 1.08f -> 1f)
    val rotationY = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    val scale = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }

    val handleFlip = {
        scope.launch {
            // 1. 抬起：轻微放大，模拟离开桌面
            scale.animateTo(
                targetValue = 1.08f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )

            // 2. 翻转：沿 Y 轴旋转 180°
            val targetRotation = if (!isFlip) 180f else 0f
            rotationY.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
            )
            isFlip = !isFlip

            // 3. 落下：恢复原始大小，模拟落回桌面
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
    }

    // 旋转超过 90 度时展示背面
    val isShowingBack = rotationY.value > 90f

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.rotationY = rotationY.value
                // 增加 3D 摄像机视距，防止翻转时透视失真严重
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 去除原生水波纹，保持翻牌质感
            ) {
                if (!scale.isRunning && !rotationY.isRunning) {
                    handleFlip()
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isShowingBack) backFaceColor else frontFaceColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 当翻到背面时，将内部内容再沿 Y 轴翻转 180°，防止文字/图标镜像倒置
                .graphicsLayer {
                    if (isShowingBack) {
                        this.rotationY = 180f
                    }
                },
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                if (isShowingBack) {
                    backFaceContent()
                } else {
                    frontFaceContent()
                }
            }
        }
    }
}