package com.example.learncompose.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.drawscope.Stroke as DrawStroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 定义单次笔画的数据结构
data class Stroke(
    val points: List<Offset>,
    val color: Color = Color.Black,
    val width: Dp = 4.dp
)

@Composable
fun DrawingBoardScreen() {
    // 保存所有已经绘制完成的笔画
    val strokes = rememberSaveable { mutableStateListOf<Stroke>() }
    // 保存当前正在绘制的笔画的坐标点
    val currentPoints = rememberSaveable { mutableStateListOf<Offset>() }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        currentPoints.add(offset)
                    },
                    onDrag = { change, _ ->
                        change.consume() // 消费掉事件，防止上层父布局拦截
                        currentPoints.add(change.position)
                    },
                    onDragEnd = {
                        // 笔画结束，保存到历史记录中，并清空当前点阵
                        strokes.add(Stroke(points = currentPoints.toList()))
                        currentPoints.clear()
                    }
                )
            }
    ) {
        // 1. 绘制历史笔画
        strokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path().apply {
                    moveTo(stroke.points.first().x, stroke.points.first().y)
                    for (i in 1 until stroke.points.size) {
                        lineTo(stroke.points[i].x, stroke.points[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = stroke.color,
                    style = DrawStroke(width = stroke.width.toPx())
                )
            }
        }

        // 2. 实时绘制当前正在画的笔画
        if (currentPoints.size > 1) {
            val path = Path().apply {
                moveTo(currentPoints.first().x, currentPoints.first().y)
                for (i in 1 until currentPoints.size) {
                    lineTo(currentPoints[i].x, currentPoints[i].y)
                }
            }
            drawPath(
                path = path,
                color = Color.Black,
                style = DrawStroke(width = 4.dp.toPx())
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    DrawingBoardScreen()
}