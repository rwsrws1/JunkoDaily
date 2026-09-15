package com.example.learncompose.feature.routine.components

import android.os.Build
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learncompose.feature.routine.R

@Composable
fun FullscreenCustomOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    // 1. 拦截系统返回键
    if (visible) {
        BackHandler {
            onDismiss()
        }
    }

    // 2. 窗口级模糊处理 (Android 12+)
//    val context = LocalContext.current
//    DisposableEffect(visible) {
//        if (visible && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val window = (context as? android.app.Activity)?.window
//            window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
//            window?.attributes = window.attributes?.apply {
//                blurBehindRadius = 60
//            }
//        }
//        onDispose {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                val window = (context as? android.app.Activity)?.window
//                window?.attributes = window.attributes?.apply {
//                    blurBehindRadius = 0
//                }
//            }
//        }
//    }

    // 3. 动画组合：渐变 + 从下方弹起 + 弹性缩放
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                ) +
                scaleIn(
                    initialScale = 0.85f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
                ),
        exit = fadeOut(animationSpec = spring(stiffness = Spring.StiffnessHigh)) +
                slideOutVertically(
                    targetOffsetY = { it / 4 },
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                ) +
                scaleOut(
                    targetScale = 0.9f,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                )
    ) {
        // 全屏遮罩层
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 半透明暗色背景
                .background(Color.Black.copy(alpha = 0.45f))
                // 低版本 Android (Android 11 及以下) 回退的 Compose 自带毛玻璃
                .then(
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        Modifier.blur(16.dp)
                    } else Modifier
                )
                // 点击背景关闭
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // 弹窗主体
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f) // 左右留出 12% 边距
                    .fillMaxHeight(0.65f) // 上下留出 35% 边距
                    // 关键：吃掉卡片区域的点击事件，防止点击卡片内部透传到背景关闭
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
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CardPickerDialog()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardPickerDialog(modifier: Modifier = Modifier) {
    var changeShape by remember { mutableStateOf(false) }

    // 状态管理
    var selectedShapeIndex by remember { mutableIntStateOf(1) }
    var selectedColorIndex by remember { mutableIntStateOf(3) }
    var selectedCategoryIndex by remember { mutableIntStateOf(1) }

    val categories = listOf("Weather", "Shapes", "Cinematic")

    // 调色板定义
    val colorList = listOf(
        Color(0xFFB3739C),
        Color(0xFFA6855A),
        Color(0xFFB55D6C),
        Color(0xFF5A83BA),
        Color(0xFF5A5E5B)
    )

    // Shape 定义
    val shapeList: List<Shape> = remember {
        listOf(
            CircleShape,
            RoundedCornerShape(12.dp),
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 4.dp),
            RoundedCornerShape(CornerSize(30)),
            RoundedCornerShape(18.dp)
        )
    }

    val slideState = rememberSliderState(
        value = 0.5f,
        steps = 0, trackRange = 0f..1f
    )

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
                    changeShape = !changeShape
                }
            ) {
                Text("test")
            }
            ImageAreaCard(Modifier.fillMaxWidth(0.8f), changeShape)
        }
        // 2. 中间编辑操作卡片
        Card(
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C282D) // 深灰暗色调卡片
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    shapeList.forEachIndexed { index, shape ->
                        val isSelected = selectedShapeIndex == index
                        val bgColor = if (isSelected) Color(0xFF6B8DB9) else Color(0xFF454045)
                        val itemColor = if (isSelected) Color.White else Color(0xFF9E9A9E)

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { selectedShapeIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(shape)
                                    .background(itemColor)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))
                // 第二行：Color 颜色选择器
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colorList.forEachIndexed { index, color ->
                        val isSelected = selectedColorIndex == index
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.dp, Color.White, CircleShape)
                                    } else Modifier
                                )
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }
                // 第三行：M3 Slider 调节滑块
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
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            categories.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = categories.size
                    ),
                    onClick = { selectedCategoryIndex = index },
                    selected = selectedCategoryIndex == index,
                    icon = {
                        if (selectedCategoryIndex == index) {
                            Icon(
                                painterResource(R.drawable.mop_24px),
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                            )
                        }
                    },
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = Color.White,
                        activeContentColor = Color.Black,
                        inactiveContainerColor = Color(0xFF2C282D),
                        inactiveContentColor = Color.White
                    )
                ) {
                    Text(text = label)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Preview
@Composable
private fun Preview() {
    FullscreenCustomOverlay(true, {})
}