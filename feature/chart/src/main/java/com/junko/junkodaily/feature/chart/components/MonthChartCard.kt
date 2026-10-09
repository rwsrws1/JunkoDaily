package com.junko.junkodaily.feature.chart.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junko.junkodaily.core.designsystem.ContainerLowest
import com.junko.junkodaily.core.designsystem.property.PresetImage
import com.junko.junkodaily.core.designsystem.property.PresetShape
import com.junko.junkodaily.core.designsystem.toComposeColor
import com.junko.junkodaily.core.model.RoutineCardsAndLogs

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MonthChartCard(
    modifier: Modifier = Modifier,
    cardAndLog: RoutineCardsAndLogs = RoutineCardsAndLogs(),
    daysInMonths: Int = 31,
    completedDays: Set<Int> = setOf(1),
    onChartClick: () -> Unit = {},
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform,
    cardId: Long,
) {
    val boxShape = PresetShape.fromName(cardAndLog.card.cardShape).polygon.toShape()
    val imageId = PresetImage.fromResName(cardAndLog.card.cardImage).resId
    val cardColor = cardAndLog.card.cardColor.toComposeColor()
    val cardText = cardAndLog.card.cardText
    Card(
        onClick = {
            onChartClick()
        },
        modifier = modifier.aspectRatio(1f / 1.1f),
        colors = CardDefaults.cardColors(
            containerColor = ContainerLowest
        ),
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            cardText,
            Modifier.align(Alignment.CenterHorizontally).padding(horizontal = 10.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(10.dp))
        Box(Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 10.dp)) {
            MonthCalendarGrid(
                daysInMonths = daysInMonths,
                completedDays = completedDays,
                activeColor = cardColor,
                modifier = Modifier.fillMaxSize(),
                boxShape = boxShape
            )
            with(sharedTransitionScope) {
                Image(
                    painterResource(imageId), null,
                    Modifier.align(Alignment.Center).sharedBounds(
                        sharedContentState = rememberSharedContentState("card_bounds${cardId}"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = boundsTransform,
                        clipInOverlayDuringTransition = OverlayClip(MaterialTheme.shapes.medium)),
                    alpha = 0.2f
                )
            }
        }

        Spacer(Modifier.height(5.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))

            Box(Modifier
                .size(15.dp)
                .clip(boxShape)
                .background(cardColor))
            Spacer(Modifier.weight(0.05f))
            Box {
                Text("00", Modifier.alpha(0f))
                Text("${completedDays.size}")
            }
            Spacer(Modifier.weight(0.1f))

            Box(Modifier
                .size(15.dp)
                .clip(boxShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest))
            Spacer(Modifier.weight(0.05f))
            Box {
                Text("00", Modifier.alpha(0f))
                Text("${daysInMonths - completedDays.size}")
            }
            Spacer(Modifier.weight(0.1f))

//            VerticalDivider(Modifier.fillMaxHeight(0.6f))
//            Spacer(Modifier.weight(0.1f))
//            Text("${completedDays.size * 100 / daysInMonths}%")
//            Spacer(Modifier.weight(0.1f))
        }
        Spacer(Modifier.height(5.dp))
    }
}

/**
 * 替代内层 LazyVerticalGrid 的轻量级日历网格组件
 */
@Composable
private fun MonthCalendarGrid(
    daysInMonths: Int,
    completedDays: Set<Int>,
    activeColor: Color,
    modifier: Modifier = Modifier,
    boxShape: Shape
) {
    // 假设前导有 2 个空位置
    val firstDayOffset = 2
    val totalSlots = firstDayOffset + daysInMonths
    val rows = (totalSlots + 6) / 7

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        for (rowIndex in 0 until rows) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                for (columnIndex in 0 until 7) {
                    val slotIndex = rowIndex * 7 + columnIndex
                    val dayNumber = slotIndex - firstDayOffset + 1

                    if (slotIndex in firstDayOffset until totalSlots) {
                        val isCompleted = completedDays.contains(dayNumber)
                        DayBox(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f)
                                .aspectRatio(1f / 1f)
//                                .clip(MaterialTheme.shapes.extraSmall)
                                .clip(boxShape)
                                .background(
                                    if (isCompleted) activeColor
                                    else MaterialTheme.colorScheme.surfaceContainerHighest
                                ),
                            number = dayNumber,
                        )
                    } else {
                        // 空白的填充格
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DayBox(modifier: Modifier = Modifier, number: Int) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$number",
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            color = ContainerLowest,
            letterSpacing = 0.sp,
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SharedTransitionLayout(

    ) {
        AnimatedContent(
            targetState = true
        ) { isShow ->
            if (isShow) {
                MonthChartCard(sharedTransitionScope = this@SharedTransitionLayout, animatedVisibilityScope = this@AnimatedContent, boundsTransform = BoundsTransform { initialBounds, targetBounds -> tween<Rect>() }, cardId = 0L)

            }
        }
    }
}