package com.junko.junkodaily.feature.chart.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junko.junkodaily.core.designsystem.ContainerLowest
import com.junko.junkodaily.core.designsystem.OnSurface
import com.junko.junkodaily.core.designsystem.components.ImageAreaCard
import com.junko.junkodaily.core.designsystem.property.PresetShape
import com.junko.junkodaily.core.designsystem.toComposeColor
import com.junko.junkodaily.core.designsystem.toCompositeOverSurface
import com.junko.junkodaily.core.model.RoutineCardsAndLogs
import com.junko.junkodaily.feature.chart.FloatAnimatableSaver
import com.junko.junkodaily.feature.chart.composeColor

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun YearChartCard(
    cardModifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs = RoutineCardsAndLogs(),
    completedMonthDays: List<Float> = listOf(1f),
    currentYear: Int = 2026,
) {
    val cardColor = cardAndLog.card.cardColor.toComposeColor()
    val cardShape = PresetShape.fromName(cardAndLog.card.cardShape).polygon
    val cardText = cardAndLog.card.cardText
    val cardImage = cardAndLog.card.cardImage
    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(0.1f))
        Card(Modifier
            .fillMaxWidth(0.3f)
            .aspectRatio(1f / 1.5f)
            .align(Alignment.CenterHorizontally),
            colors = CardDefaults.cardColors(
                containerColor = cardColor.toCompositeOverSurface()),
            shape = MaterialTheme.shapes.large
        ) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.weight(1f))
                Box(Modifier.fillMaxWidth(0.9f), contentAlignment = Alignment.Center) {
                    ImageAreaCard(
                        modifier = cardModifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f),
                        targetShape = cardShape,
                        imageColor =  cardColor,
                        selectImage = cardImage,
                        onImageClick = null,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = cardText,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                )
                Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.weight(0.1f))
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Card(
                modifier = chartModifier
                    .fillMaxWidth(0.9f)
                    .aspectRatio(1f / 0.6f),
                colors = CardDefaults.cardColors(
                    containerColor = ContainerLowest
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    Modifier.fillMaxSize()
                ) {
                    Spacer(Modifier.weight(0.3f))
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.weight(0.3f))
                        Text(
                            text = "$currentYear",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.weight(4f))
                        Box(Modifier
                            .size(15.dp)
                            .clip(cardShape.toShape())
                            .background(cardColor)
                        )
                        Spacer(Modifier.weight(0.1f))
                        Box {
                            Text(text = "mmm", modifier = Modifier.alpha(0f))
                            Text(
                                text = "${completedMonthDays.sum().toInt()}",
                                maxLines = 1,
                                textAlign = TextAlign.End
                            )
                        }
                        Spacer(Modifier.weight(0.1f))
                    }
                    Spacer(Modifier.weight(0.1f))
                    Box(
                        Modifier
                            .weight(4f)
                            .padding(horizontal = 10.dp),
                    ) {
                        AnimatedBarChart(data = completedMonthDays, color = cardAndLog.card.composeColor)
                    }
                    Spacer(Modifier.weight(0.1f))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }

}

@Composable
fun AnimatedBarChart(
    data: List<Float>,
    color: Color,
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.labelSmall.copy(color = OnSurface)
    val textStyleBottom = MaterialTheme.typography.bodyMedium.copy(color = OnSurface)

    // 柱子升起的动画
    val progress = remember { Animatable(0f) }

    // 平均线延伸/显示的动画 (0f -> 1f)
    val lineProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        progress.snapTo(0f)
        lineProgress.snapTo(0f)

        // 先播放柱子升起动画，再播放平均线动画（或使用 launch 并行）
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
        lineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // 计算平均值
    val averageValue = if (data.isNotEmpty()) data.average().toFloat() else 0f

    Canvas(modifier = Modifier.fillMaxSize()) {
        val maxValue = 31f
        val barCount = data.size
        val spacing = 4.dp.toPx()
        val topPadding = 20.dp.toPx()
        val bottomPadding = 20.dp.toPx()
        val chartHeight = size.height - topPadding - bottomPadding
        val totalSpacing = spacing * (barCount - 1)
        val barWidth = if (barCount > 0) (size.width - totalSpacing) / barCount else 0f
        val barBottomY = size.height - bottomPadding

        // --- 1. 绘制柱子与文本 ---
        data.forEachIndexed { index, value ->
            val targetHeight = (value / maxValue) * chartHeight
            val currentHeight = targetHeight * progress.value
            val xOffset = index * (barWidth + spacing)
            val barTopY = barBottomY - currentHeight

            // 绘制柱体
            drawRoundRect(
                color = color,
                topLeft = Offset(xOffset, barTopY),
                size = Size(barWidth, currentHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // 绘制顶部 Value 文本
            if (value > 0) {
                val valueText = value.toInt().toString()
                val valueLayoutResult = textMeasurer.measure(valueText, textStyle)
                val valueX = xOffset + (barWidth - valueLayoutResult.size.width) / 2
                val valueY = barTopY - valueLayoutResult.size.height - 2.dp.toPx()
                drawText(
                    textMeasurer = textMeasurer,
                    text = valueText,
                    style = textStyle,
                    topLeft = Offset(valueX, valueY)
                )
            }

            // 绘制底部 Index 文本
            val indexText = (index + 1).toString()
            val indexLayoutResult = textMeasurer.measure(indexText, textStyleBottom)
            val indexX = xOffset + (barWidth - indexLayoutResult.size.width) / 2
            val indexY = barBottomY + 4.dp.toPx()
            drawText(
                textMeasurer = textMeasurer,
                text = indexText,
                style = textStyleBottom,
                topLeft = Offset(indexX, indexY)
            )
        }

        // --- 2. 绘制带动画效果的平均线 ---
        if (data.isNotEmpty() && averageValue > 0) {
            // 平均线对应的真实 Target Y 坐标
            val avgTargetHeight = (averageValue / maxValue) * chartHeight
            val avgY = barBottomY - avgTargetHeight

            // 方案 A：从左到右拉伸延伸动画
            val lineEndX = size.width * lineProgress.value

            // 虚线样式：5dp 宽虚线，5dp 间隔
            val strokeWidth = 1.5.dp.toPx()
            val dashPathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                phase = 0f
            )

            // 绘制虚线平均线
            drawLine(
                color = color.copy(alpha = 0.7f),
                start = Offset(0f, avgY),
                end = Offset(lineEndX, avgY),
                strokeWidth = strokeWidth,
                pathEffect = dashPathEffect
            )

            // 当线条展开到一定程度时，绘制右侧/左侧的“均值文字标签”
            if (lineProgress.value > 0.3f) {
                val avgText = "Avg: %.1f".format(averageValue)
                val avgTextStyle = textStyle.copy(
                    color = color.copy(alpha = 0.9f),
                    fontSize = 10.sp
                )
                val avgLayoutResult = textMeasurer.measure(avgText, avgTextStyle)

                // 将文字靠右对齐放置在虚线上方
                val avgTextX = size.width - avgLayoutResult.size.width - 4.dp.toPx()
                val avgTextY = avgY - avgLayoutResult.size.height - 2.dp.toPx()

                drawText(
                    textMeasurer = textMeasurer,
                    text = avgText,
                    style = avgTextStyle,
                    topLeft = Offset(avgTextX, avgTextY),
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    YearChartCard()
}