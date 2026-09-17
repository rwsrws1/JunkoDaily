package com.example.learncompose.feature.routine.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ButtonGroupDefaults.connectedButtonCheckedShape
import androidx.compose.material3.ButtonGroupDefaults.connectedMiddleButtonPressShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonSize
import androidx.compose.material3.rememberSliderState
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.layout.VerticalRuler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import com.example.learncompose.core.designsystem.PresetFiveRandomColor
import com.example.learncompose.core.designsystem.PresetFiveRandomShape
import com.example.learncompose.core.designsystem.adjustSaturationAndLightness
import com.example.learncompose.core.designsystem.icons.AppIcons
import com.example.learncompose.core.designsystem.toComposeColor
import kotlin.text.get

@Composable
fun FullscreenCustomOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (visible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(0.4f))
        ) {
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(), initialAlpha = 0.5f) +
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                ) +
                scaleIn(
                    initialScale = 0.6f,
                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                ),
        exit = fadeOut(animationSpec = MaterialTheme.motionScheme.slowSpatialSpec(), targetAlpha = 0.5f) +
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                ) +
                scaleOut(
                    targetScale = 0.6f,
                    animationSpec = MaterialTheme.motionScheme.slowSpatialSpec()
                )
    ) {
        BackHandler {
            onDismiss()
        }
        Box(Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CardPickerDialog()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CardPickerDialog(modifier: Modifier = Modifier) {
    var selectedShapeIndex by remember { mutableIntStateOf(0) }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("颜色", "形状", "图案")
    val categoriesIcons = listOf(AppIcons.routineFilled, AppIcons.chartFilled, AppIcons.spendFilled)
    val colorList = remember { PresetFiveRandomColor }
    val shapeList = remember { PresetFiveRandomShape }

    val slideState = rememberSliderState(
        value = 1f,
        steps = 0, trackRange = 0f..2f
    )

    val interactionSources = remember { List(shapeList.size) { MutableInteractionSource() } }

    val targetColor = colorList[selectedColorIndex].toComposeColor()
        .adjustSaturationAndLightness(saturationFactor = slideState.value)
    val animColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = MaterialTheme.motionScheme.slowEffectsSpec()
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .fillMaxHeight(0.65f),
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(animColor.copy(0.3f).
                compositeOver(MaterialTheme.colorScheme.surfaceContainerLowest))
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageAreaCard(
                Modifier.fillMaxWidth(0.8f),
                targetShape = shapeList[selectedShapeIndex],
                imageColor = animColor
            )

            Spacer(Modifier.weight(0.2f))

            Card(
                modifier = Modifier.fillMaxWidth(0.9f).weight(4f),
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
                    Spacer(Modifier.weight(0.1f))

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
                                                contentColor = MaterialTheme.colorScheme.surface.copy(0.2f),
                                                checkedContainerColor = MaterialTheme.colorScheme.surface.copy(0.2f),
                                                checkedContentColor = MaterialTheme.colorScheme.inverseSurface
                                            ),
                                            contentPadding = contentPadding,
                                        ) {
                                            Box(Modifier.size(25.dp).clip(shapeList[index].toShape())
                                                .background(MaterialTheme.colorScheme.surface))
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

                    Spacer(Modifier.weight(0.1f))

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
                                    containerColor = color.toComposeColor(),
                                    contentColor = MaterialTheme.colorScheme.inverseSurface,
                                    checkedContainerColor = color.toComposeColor(),
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

                    Spacer(Modifier.weight(0.1f))

                    Slider(state = slideState,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        onValueChange = { slideState.value = it },
                        onValueChangeFinished = null,
                        colors = SliderDefaults.colors(),
                        interactionSource = remember { MutableInteractionSource() })

                    Spacer(Modifier.weight(0.1f))
                }
            }

            Spacer(Modifier.weight(0.2f))

            Box(Modifier
                .fillMaxWidth(0.75f).weight(1f)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.inverseSurface)
                .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(Modifier.fillMaxWidth()) {
                    categories.forEachIndexed { index, label ->
                        ToggleButton(
                            modifier = Modifier.weight(1f),
                            checked = selectedCategoryIndex == index,
                            onCheckedChange = { selectedCategoryIndex = index },
                            buttonSize = ToggleButtonSize.ExtraSmall,
                            shapes = ButtonGroupDefaults.connectedMiddleButtonShapes(),
                            colors = ToggleButtonDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.inverseSurface,
                                contentColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            contentPadding = PaddingValues(0.dp),
                        ) {
                            if (selectedCategoryIndex == index) {
                                Icon(
                                    painterResource(categoriesIcons[index]),
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

            Spacer(Modifier.weight(0.1f))
        }
    }

}

@Preview
@Composable
private fun Preview() {
    FullscreenCustomOverlay(true, {})
}