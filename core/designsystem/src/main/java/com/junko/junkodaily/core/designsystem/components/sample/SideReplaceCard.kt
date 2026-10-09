package com.junko.junkodaily.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
fun SlideReplaceCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isFront by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // 偏移动画 (0f -> 1f -> 0f)
    val transitionProgress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    val handleSlide = {
        scope.launch {
            // 1. 划走当前卡片
            transitionProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
            )
            // 2. 状态切换
            isFront = !isFront
            // 3. 新卡片回弹归位
            transitionProgress.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
            )
        }
    }

    val p = transitionProgress.value

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                // 向上滑出并带轻微倾斜与半透明淡出
                translationY = -p * 350f
                rotationZ = -p * 8f
                alpha = 1f - (p * 0.45f)
                scaleX = 1f - (p * 0.05f)
                scaleY = 1f - (p * 0.05f)
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!transitionProgress.isRunning) handleSlide()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFront) frontFaceColor else backFaceColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isFront) frontFaceContent() else backFaceContent()
            }
        }
    }
}