package com.example.learncompose.feature.experiment.features.home.page.char

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val FloatAnimatableSaver = Saver<Animatable<Float, AnimationVector1D>, Float>(
    save = { it.value }, // 保存时，只提取当前的 Float 值
    restore = { Animatable(it) } // 恢复时，用保存的 Float 值重新创建 Animatable
)

/**
 * 图表动效演示主界面 (可直接放到 setContent 中预览)
 */
@Composable
fun ChartDemoScreen() {
    val sampleData = remember {
        listOf(25f, 50f, 15f, 80f, 40f, 65f)
    }
    val chartColors = remember {
        listOf(
            0xFF5C6BC0, 0xFF26A69A, 0xFFEF5350,
            0xFFFFCA28, 0xFFAB47BC, 0xFF29B6F6
        )
    }.map {
        Color(it)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        ChartSection("柱状图 (向上生长动效)") {
            AnimatedBarChart(data = sampleData, colors = chartColors)
        }

        ChartSection("折线图 (向右展开动效)") {
            AnimatedLineChart(data = sampleData, lineColor = Color(0xFF5C6BC0))
        }

        ChartSection("饼图 (扇形展开动效)") {
            AnimatedPieOrDonutChart(data = sampleData, colors = chartColors, isDonut = false)
        }

        ChartSection("环形图 (扇形展开动效)") {
            AnimatedPieOrDonutChart(data = sampleData, colors = chartColors, isDonut = true)
        }
    }
}

@Composable
fun ChartSection(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = title, fontSize = 18.sp, color = Color.DarkGray)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            content()
        }
    }
}

/**
 * 1. 柱状图实现
 */
@Composable
fun AnimatedBarChart(data: List<Float>, colors: List<Color>) {
    // 创建一个从 0 到 1 的动画进度状态
    val progress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxValue = data.maxOrNull() ?: 0f
        if (maxValue == 0f) return@Canvas

        val barCount = data.size
        // 计算柱子之间的间距和柱子的宽度
        val spacing = 16.dp.toPx()
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = (size.width - totalSpacing) / barCount

        data.forEachIndexed { index, value ->
            // 高度乘以动画进度 progress，实现从下往上生长的动效
            val targetHeight = (value / maxValue) * size.height
            val currentHeight = targetHeight * progress.value

            val xOffset = index * (barWidth + spacing)
            val yOffset = size.height - currentHeight // 从底部开始画

            drawRoundRect(
                color = colors[index % colors.size],
                topLeft = Offset(xOffset, yOffset),
                size = Size(barWidth, currentHeight),
                cornerRadius = CornerRadius(4.dp.toPx()) // 顶部加点圆角更好看
            )
        }
    }
}

/**
 * 2. 折线图实现
 */
@Composable
fun AnimatedLineChart(data: List<Float>, lineColor: Color) {
    val progress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxValue = data.maxOrNull() ?: 0f
        if (maxValue == 0f || data.size < 2) return@Canvas

        val xStep = size.width / (data.size - 1)
        val path = Path()

        // 构建折线的 Path
        data.forEachIndexed { index, value ->
            val x = index * xStep
            // 翻转 Y 轴，因为 Canvas 的 0,0 在左上角
            val y = size.height - ((value / maxValue) * size.height)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // 核心动效：使用 clipRect 裁剪可见区域，配合 progress 实现从左向右逐渐绘制的效果
        clipRect(right = size.width * progress.value) {
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            // 顺便在每个数据点画个小圆圈
            data.forEachIndexed { index, value ->
                val x = index * xStep
                val y = size.height - ((value / maxValue) * size.height)
                drawCircle(
                    color = lineColor,
                    radius = 6.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill
                )
                // 画个白心让圆点更好看
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = Offset(x, y),
                    style = Fill
                )
            }
        }
    }
}

/**
 * 3 & 4. 饼图/环形图实现 (二者原理一致，只是画法不同)
 */
@Composable
fun AnimatedPieOrDonutChart(data: List<Float>, colors: List<Color>, isDonut: Boolean) {
    val progress = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val total = data.sum()
        if (total == 0f) return@Canvas

        // 计算图表的半径（取宽高最小值的一半，留一点边距）
        val radius = (minOf(size.width, size.height) / 2) * 0.9f
        // 居中偏移
        val centerOffset = Offset(size.width / 2, size.height / 2)
        val arcSize = Size(radius * 2, radius * 2)
        val arcTopLeft = Offset(centerOffset.x - radius, centerOffset.y - radius)

        var currentStartAngle = -90f // 从正上方 12 点钟方向开始

        data.forEachIndexed { index, value ->
            // 当前扇形应该占用的总角度
            val targetSweepAngle = (value / total) * 360f
            // 根据动画进度计算当前帧应该绘制的角度
            val currentSweepAngle = targetSweepAngle * progress.value

            drawArc(
                color = colors[index % colors.size],
                startAngle = currentStartAngle,
                sweepAngle = currentSweepAngle,
                useCenter = !isDonut, // 饼图需要连接圆心，环形图不需要
                topLeft = arcTopLeft,
                size = arcSize,
                style = if (isDonut) {
                    // 如果是环形图，使用 Stroke 描边模式
                    Stroke(width = radius * 0.4f) 
                } else {
                    // 如果是饼图，使用 Fill 填充模式
                    Fill
                }
            )
            // 累加起始角度，准备画下一个扇形
            currentStartAngle += targetSweepAngle 
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    ChartDemoScreen()
}