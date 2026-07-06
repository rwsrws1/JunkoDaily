package com.example.learncompose.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.example.learncompose.R

@Composable
fun ScratchCardScreen() {
    // 记录擦除轨迹的路径
    val erasePaths = remember { mutableStateListOf<Path>() }
    var currentErasePath by remember { mutableStateOf<Path?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 【第一层：底层背景】（手指涂抹后要露出来的画面）
        AsyncImage(
            model = R.drawable.girl, // 替换为你的精美背景图
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 【第二层：表层遮罩画布】
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // ✨ 极其关键：通过开启离屏缓冲，让 BlendMode.Clear 只作用于当前 Canvas，不影响底部的 Image
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentErasePath = Path().apply {
                                moveTo(offset.x, offset.y)
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentErasePath?.lineTo(change.position.x, change.position.y)

                            // 💡 技巧：Compose 的 Path 内部变化不会主动触发重绘，
                            // 我们通过给它重新赋个值或加入列表来强行触发布局刷新
                            val p = currentErasePath
                            currentErasePath = null
                            currentErasePath = p
                        },
                        onDragEnd = {
                            currentErasePath?.let { erasePaths.add(it) }
                            currentErasePath = null
                        }
                    )
                }
        ) {
            // 1. 首先把整个画布涂成纯白色（或者任意你想作为刮刮乐表面的颜色）
            drawRect(color = Color(0xFFCCCCCC)) // 比如这里用灰色充当银色刮刮层

            // 2. 绘制已经画完的橡皮擦轨迹
            erasePaths.forEach { path ->
                drawPath(
                    path = path,
                    color = Color.Transparent, // 擦除颜色实际上无所谓，关键看 BlendMode
                    style = DrawStroke(width = 200f), // 橡皮擦的粗细
                    blendMode = BlendMode.Clear       // 🌟 核心：使用 Clear 模式抠空灰色遮罩
                )
            }

            // 3. 实时绘制当前正在擦除的轨迹
            currentErasePath?.let { path ->
                drawPath(
                    path = path,
                    color = Color.Transparent,
                    style = DrawStroke(width = 200f),
                    blendMode = BlendMode.Clear
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ScratchCardScreen()
}