package com.junko.junkodaily.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun DepthFlipCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isShowingBack by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val rotationX = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }
    val scale = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }

    val handleFlip = {
        scope.launch {
            // 1. 快速向 Z 轴纵深下沉缩小
            scale.animateTo(
                targetValue = 0.85f,
                animationSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing)
            )

            // 2. 沿 X 轴翻滚（纵向翻页）
            val targetRotation = if (!isShowingBack) 180f else 0f
            rotationX.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
            )
            isShowingBack = !isShowingBack

            // 3. 弹性恢复原始尺寸，带弹性阻尼质感
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val isFlipped = rotationX.value > 90f

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                this.scaleX = scale.value
                this.scaleY = scale.value
                this.rotationX = rotationX.value
                cameraDistance = 14f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!scale.isRunning && !rotationX.isRunning) handleFlip()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) backFaceColor else frontFaceColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // X 轴翻转后内容矫正
                    if (isFlipped) this.rotationX = 180f
                }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isFlipped) backFaceContent() else frontFaceContent()
            }
        }
    }
}