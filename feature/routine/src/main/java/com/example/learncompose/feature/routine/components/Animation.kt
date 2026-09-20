package com.example.learncompose.feature.routine.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun AnimatedGridItem(
    index: Int,
    delayPerItem: Int = 100,
    content: @Composable () -> Unit
) {
    // 使用 rememberSaveable 记录，确保配置变更或滑动复用时不会重复播放入场动画
    var hasAnimated by rememberSaveable { mutableStateOf(false) }
    val index by rememberUpdatedState(index)

    val alpha = remember { Animatable(if (hasAnimated) 1f else 0f) }
    val offsetY = remember { Animatable(if (hasAnimated) 0f else 40f) }

    LaunchedEffect(Unit) {
        if (!hasAnimated) {
            // 取余计算，防止排名偏后的 Item 延迟过长
            val delay = index * delayPerItem

            launch {
                alpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(300, delayMillis = delay, easing = FastOutSlowInEasing)
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(300, delayMillis = delay, easing = FastOutSlowInEasing)
                )
            }
            hasAnimated = true
        }
    }

    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha.value
            this.translationY = offsetY.value
        }
    ) {
        content()
    }
}