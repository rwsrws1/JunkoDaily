package com.example.learncompose.feature.routine.components

import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupDefaults.connectedButtonCheckedShape
import androidx.compose.material3.ButtonGroupDefaults.connectedMiddleButtonPressShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.Ruler
import androidx.compose.ui.layout.RulerScope
import androidx.compose.ui.layout.VerticalRuler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import com.example.learncompose.core.designsystem.PresetColorList
import com.example.learncompose.core.designsystem.PresetFiveRandomColor
import com.example.learncompose.core.designsystem.PresetFiveRandomShape
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.feature.routine.R

@Composable
fun FullscreenCustomOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        BackHandler {
            onDismiss()
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(0.4f))
                // 点击背景关闭
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = onDismiss
                )
        ) {
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium), initialAlpha = 0.5f) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                ) +
                scaleIn(
                    initialScale = 0.5f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)
                ),
        exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium), targetAlpha = 0.5f) +
                slideOutVertically(
                    targetOffsetY = { it / 2 },
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                ) +
                scaleOut(
                    targetScale = 0f,
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
    ) {
        Box(Modifier.fillMaxSize().clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {}
        ),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.65f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    ),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardPickerDialog()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardPickerDialog(modifier: Modifier = Modifier) {
    var changeShape by remember { mutableStateOf(false) }
    val options = listOf("形状", "颜色", "图案")

    // 状态管理
    var lastShapeIndex by remember { mutableIntStateOf(0) }
    var selectedShapeIndex by remember { mutableIntStateOf(0) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("颜色", "形状", "Cinematic")

    val colorList = remember { PresetFiveRandomColor }
    val shapeList = remember { PresetFiveRandomShape }


    val slideState = rememberSliderState(
        value = 0.5f,
        steps = 0, trackRange = 0f..1f
    )

    val unCheckedIcons = listOf(AppIcons.routine, AppIcons.chart, AppIcons.spend)
    val checkedIcons = listOf(AppIcons.routineFilled, AppIcons.chartFilled, AppIcons.spendFilled)
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val interactionSources = remember { List(shapeList.size) { MutableInteractionSource() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF8B9CB2)) // 匹配背景灰蓝色
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            TextButton(
                onClick = {

                }
            ) {
                Text("test")
            }
            ImageAreaCard(Modifier.fillMaxWidth(0.8f), shapeList[lastShapeIndex], shapeList[selectedShapeIndex])
        }
        // 2. 中间编辑操作卡片
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.inverseSurface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 第一行：Shape 形状选择器
                Spacer(Modifier.height(10.dp))

                VerticalRuler()
                val bgColor = Color(0xFF6B8DB9)
                val itemColor = Color(0xFF9E9A9E)

                Box(Modifier.fillMaxWidth(1f)) {
                    ButtonGroup(
                        modifier = Modifier.fillMaxWidth(),
                        overflowIndicator = { menuState ->
//                            ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
                        },
                        expandedRatio = ButtonGroupDefaults.ExpandedRatio,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        shapeList.forEachIndexed { index, label ->
                            customItem(
                                buttonGroupContent = {
                                    val contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
                                    val layoutDirection = LocalLayoutDirection.current

                                    ToggleButton(
                                        modifier =
                                            Modifier.animateWidth(
                                                interactionSource = interactionSources[index],
                                                compressionLimit =
                                                    contentPadding.calculateEndPadding(layoutDirection),
                                            )
                                        ,
                                        checked = selectedShapeIndex == index,
                                        onCheckedChange = { selectedShapeIndex = index },
                                        interactionSource = interactionSources[index],
                                        shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(
                                            shape = CircleShape,
                                            pressedShape = connectedMiddleButtonPressShape,
                                            checkedShape = connectedButtonCheckedShape,
                                        ),
                                        colors = ToggleButtonDefaults.colors(
                                            containerColor = MaterialTheme.colorScheme.scrim.copy(0.2f),
                                            contentColor = MaterialTheme.colorScheme.primaryContainer,
                                            checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            checkedContentColor = MaterialTheme.colorScheme.inverseSurface
                                            ),
                                        contentPadding = contentPadding,
                                    ) {
                                        Box(Modifier.size(25.dp).clip(shapeList[index].toShape())
                                            .background(itemColor))
                                    }
                                },
                                menuContent = {
//                                    DropdownMenuItem(
//                                        leadingIcon = { checkedIcons[index] },
//                                        text = { Text(label) },
//                                        onClick = {},
//                                        interactionSource = interactionSources[index],
//                                    )
                                },
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))


                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    colorList.forEachIndexed { index, color ->
                        ToggleButton(
                            modifier = Modifier
                                .then(
                                    if (selectedColorIndex == index) {
                                        Modifier.border(2.dp, Color.White, MaterialTheme.shapes.medium)
                                    } else Modifier
                                )
                            ,
                            checked = selectedColorIndex == index,
                            onCheckedChange = { selectedColorIndex = index },
                            buttonSize = ToggleButtonSize.ExtraSmall,
                            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(
                                shape = CircleShape,
                                pressedShape = connectedMiddleButtonPressShape,
                                checkedShape = MaterialTheme.shapes.small,
                            ),
                            colors = ToggleButtonDefaults.colors(
                                containerColor = Color(color),
                                contentColor = MaterialTheme.colorScheme.inverseSurface,
                                checkedContainerColor = Color(color),
                                checkedContentColor = MaterialTheme.colorScheme.inverseSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        ) {
                            Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Slider(state = slideState,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = true,
                    onValueChange = { slideState.value = it },
                    onValueChangeFinished = null,
                    colors = SliderDefaults.colors(),
                    interactionSource = remember { MutableInteractionSource() })
                Spacer(Modifier.height(10.dp))
            }
        }
        Spacer(Modifier.height(5.dp))

        Box(Modifier
            .fillMaxWidth(0.75f)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.inverseSurface)
            .padding(horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, label ->
                    ToggleButton(
                        modifier = Modifier.weight(1f).wrapContentHeight(),
                        checked = selectedCategoryIndex == index,
                        onCheckedChange = { selectedCategoryIndex = index },
                        buttonSize = ToggleButtonSize.ExtraSmall,
                        shapes =
                            when (index) {
                                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            },
                        colors = ToggleButtonDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.inverseSurface,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        if (selectedCategoryIndex == index) {
                            Icon(
                                painterResource(checkedIcons[index]),
                                null,
                                Modifier.size(ButtonDefaults.iconSizeFor(ToggleButtonSize.ExtraSmall.height)),
                            )
                            Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                        }
                        Text(label)
                        Spacer(Modifier.width(ToggleButtonDefaults.IconSpacing))
                    }
                }
            }
        }

//        ButtonGroup(
//            overflowIndicator = { menuState ->
//                ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
//            },
//            expandedRatio = ButtonGroupDefaults.ExpandedRatio,
//            horizontalArrangement = Arrangement.spacedBy(2.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            options.forEachIndexed { index, label ->
//                toggleableItem(
//                    checked = selectedIndex == index,
//                    label = label,
//                    onCheckedChange = { selectedIndex = index },
//                    icon = {
//                        if (selectedIndex == index) {
//                            Icon(
//                                painterResource(if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index]),
//                                contentDescription = "Localized description",
//                            )
//                        }
//                    }
//                )
//            }
//        }

//        SingleChoiceSegmentedButtonRow(
//            modifier = Modifier.fillMaxWidth(0.8f)
//        ) {
//            categories.forEachIndexed { index, label ->
//                SegmentedButton(
//                    shape = SegmentedButtonDefaults.itemShape(
//                        index = index,
//                        count = categories.size
//                    ),
//                    onClick = { selectedCategoryIndex = index },
//                    selected = selectedCategoryIndex == index,
//                    icon = {
//                        if (selectedCategoryIndex == index) {
//                            Icon(
//                                painterResource(R.drawable.mop_24px),
//                                contentDescription = null,
//                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
//                            )
//                        }
//                    },
//                    colors = SegmentedButtonDefaults.colors(
//                        activeContainerColor = Color.White,
//                        activeContentColor = Color.Black,
//                        inactiveContainerColor = Color(0xFF2C282D),
//                        inactiveContentColor = Color.White
//                    )
//                ) {
//                    Text(text = label)
//                }
//            }
//        }

        Spacer(Modifier.height(10.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    FullscreenCustomOverlay(true, {})
}