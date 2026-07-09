package com.example.learncompose.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.learncompose.ui.theme.LearnComposeTheme
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.YearMonth
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GreetingScreen() {
    FlowRow(
        Modifier
            .fillMaxSize().verticalScroll(rememberScrollState())
    ) {
        var daysInMonth by remember { mutableIntStateOf(0) }
        var currentDate by remember { mutableStateOf(LocalDate.now()) }
        LaunchedEffect(Unit) {
            // 提取年、月、日
            val year = currentDate.year
            val month = currentDate.monthValue // 返回正常的 1-12
            val day = currentDate.dayOfMonth
            // 指定年份和月份（这里使用上面获取的当前年月）
            val yearMonth = YearMonth.of(year, month)
            // 获取该月的天数
            daysInMonth = yearMonth.lengthOfMonth()
        }
        var checkBoxState1 by remember { mutableStateOf(false) }
        var checkBoxState2 by remember { mutableStateOf(false) }
        val parentState = remember(checkBoxState1, checkBoxState2) {
            if (checkBoxState1 && checkBoxState2) {
                ToggleableState.On
            } else if (!checkBoxState1 && ! checkBoxState2) {
                ToggleableState.Off
            } else {
                ToggleableState.Indeterminate
            }
        }
        val onParentClick = {
            val p = parentState != ToggleableState.On
            checkBoxState1 = p
            checkBoxState2 = p
        }
        TriStateCheckbox(
            state = parentState,
            onClick = onParentClick,
        )
        Checkbox(
            checked = checkBoxState1,
            onCheckedChange = {
                checkBoxState1 = it
            }
        )
        Checkbox(
            checked = checkBoxState2,
            onCheckedChange = {
                checkBoxState2 = it
            }
        )

        val options = listOf("11", "22", "33")
        var selectOption by remember { mutableStateOf(options[0]) }
        options.forEach { text ->
            RadioButton(
                selected = text == selectOption,
                onClick = {
                    selectOption = text
                }
            )
            Text(text)
        }

        var isShowDialog by remember { mutableStateOf(false) }
        if (isShowDialog) {
            Dialog(
                onDismissRequest = { isShowDialog = false }
            ) {
                Card {
                    Text("确定吗?", Modifier.padding(16.dp))
                }
            }
        }
        var switchState by remember { mutableStateOf(false) }
        Switch(
            checked = switchState,
            onCheckedChange = {
                switchState = it
            }
        )

        val interactionScope = remember { MutableInteractionSource() }
        val isPress by interactionScope.collectIsPressedAsState()
        var item by remember { mutableStateOf(0) }
        val pressListener = {
            item++
            Unit
        }
        LaunchedEffect(isPress) {
            while (isPress) {
                delay(100L.coerceIn(1L, Long.MAX_VALUE).milliseconds)
                pressListener()
            }
        }
        IconButton(
            onClick = pressListener,
            interactionSource = interactionScope
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                imageVector = if (isPress) Icons.Filled.Add else Icons.Outlined.AddCircle, contentDescription = "")
        }
        Text("item$item")

        var selectedIndex by remember { mutableIntStateOf(0) }
        val segmentedButtonOptions = listOf("Day", "Month", "Week")
        SingleChoiceSegmentedButtonRow {
            segmentedButtonOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = segmentedButtonOptions.size
                    ),
                    icon = {
                        SegmentedButtonDefaults.Icon(
                            active = index == selectedIndex,
                            activeContent = {
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null)
                            }
                        )
                    },
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex,
                    label = { Text(label) }
                )
            }
        }
        val selectedOptions = remember {
            mutableStateListOf(false, false, false)
        }
        val multiChoiceSegmentedButtonOptions = listOf("Walk", "Ride", "Drive")
        val iconColor by animateColorAsState(
            targetValue = if (selectedOptions[1]) MaterialTheme.colorScheme.primary else Color.Black,
            animationSpec = spring(stiffness = Spring.StiffnessHigh)
        )
        MultiChoiceSegmentedButtonRow {
            multiChoiceSegmentedButtonOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = multiChoiceSegmentedButtonOptions.size
                    ),
                    checked = selectedOptions[index],
                    onCheckedChange = {
                        selectedOptions[index] = !selectedOptions[index]
                    },
                    icon = { SegmentedButtonDefaults.Icon(selectedOptions[index]) },
                    label = {
                        when (label) {
                            "Walk" -> Icon(
                                imageVector =
                                    Icons.Filled.ThumbUp,
                                contentDescription = "ThumbUp"
                            )
                            "Ride" -> Icon(
                                imageVector =
                                    Icons.Filled.Favorite,
                                contentDescription = "Favorite",
                                tint = iconColor
                            )
                            "Drive" -> Icon(
                                imageVector =
                                    Icons.Default.Star,
                                contentDescription = "Star"
                            )
                        }
                    }
                )
            }
        }

        var sliderState by remember { mutableFloatStateOf(0.5f) }
        Slider(
            state = SliderState(sliderState)
        )
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().height(100.dp).padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "现在日期是 ${currentDate.year},${currentDate.monthValue},${currentDate.dayOfMonth}, 这个月有$daysInMonth 天")
        }


        val itemList = remember { mutableStateListOf("邮件 1", "邮件 2", "邮件 3", "邮件 4", "邮件 5", "邮件 6", "邮件 7") }
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(300.dp).border(2.dp, color = MaterialTheme.colorScheme.surfaceVariant).padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 【避坑指南】使用 items 时必须提供唯一的 key，否则删除时动画会错乱
            items(items = itemList, key = { it }) { item ->

                // 2. 记住每个条目的滑动状态
                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        when (dismissValue) {
                            SwipeToDismissBoxValue.EndToStart -> {
                                // 情况 A：从右往左滑（通常是删除）
                                itemList.remove(item) // 执行你的删除数据源逻辑
                                true // ✨ 返回 true：允许该滑动消除动画完成，组件会消失
                            }
                            SwipeToDismissBoxValue.StartToEnd -> {
                                // 情况 B：从左往右滑（比如标记已读，但不删除）
                                // 执行标记已读逻辑...
                                false // ✨ 返回 false：组件会自动弹回原位，不会消失
                            }
                            SwipeToDismissBoxValue.Settled -> false
                        }
                    }
                )

                // 3. 核心包裹容器
                SwipeToDismissBox(
                    modifier = Modifier.clip(RoundedCornerShape(20)),
                    state = dismissState,
                    backgroundContent = {
                        // 动态计算背景色
                        val backgroundColor by animateColorAsState(
                            targetValue = when (dismissState.targetValue) {
                                SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50) // 绿
                                SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336) // 红
                                SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.surfaceDim
                            }, label = "bg_color"
                        )

                        // 动态决定内部图标靠左还是靠右
                        val alignment = when (dismissState.targetValue) {
                            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                            else -> Alignment.CenterEnd
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(backgroundColor)
                                .padding(horizontal = 20.dp),
                            contentAlignment = alignment
                        ) {
                            if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.White)
                            } else if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                Icon(Icons.Default.Email, contentDescription = "已读", tint = Color.White)
                            }
                        }
                    },
                    // 上层内容：你原本的常规列表项
                    content = {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ListItem(
                                colors = ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                headlineContent = { Text(item) },
                                supportingContent = { Text("左右滑动可以触发不同的动作") }
                            )
                        }
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingScreenPreview() {
    LearnComposeTheme {
        GreetingScreen()
    }
}
