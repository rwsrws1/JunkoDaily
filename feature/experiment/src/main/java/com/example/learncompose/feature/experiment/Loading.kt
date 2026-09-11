package com.example.learncompose.feature.experiment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Loading(modifier: Modifier = Modifier) {
    // 💡 核心：全屏透明遮罩层
    Box(
        modifier = Modifier
            .fillMaxSize()
            // 1. 拦截并消耗掉所有指针事件（点击、长按、滑动等），阻止事件向下传递
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        event.changes.forEach { it.consume() }
                    }
                }
            }
            // 2. 可选：给背景上一层淡淡的蒙层（比如 10% 透明度的黑），给用户“不可点”的视觉暗示
            .background(MaterialTheme.colorScheme.surfaceDim.copy(alpha = 0.5f))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = modifier.size(100.dp).align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Loading()
}