package com.example.learncompose.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learncompose.core.designsystem.R
import com.example.learncompose.feature.experiment.screen.AvatarSelector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(title: String = "Chunzi", onCharClick: () -> Unit = {}) {
    val scope = rememberCoroutineScope()
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    var isPhotoPickerOpen by rememberSaveable { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    isPhotoPickerOpen = true
                }
            ) {
                AvatarSelector(Modifier.size(24.dp), isPhotoPickerOpen, { isPhotoPickerOpen = false })
            }
        },
        actions = {
            IconButton(
                onClick = onCharClick
            ) {
                Icon(painter = painterResource(R.drawable.bar_chart_4_bars_24px), null)
            }
            // 2. 用 Box 作为锚点，确保菜单永远对齐这个按钮的右上角
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {

//                            IconButton(onClick = { menuExpanded = true }) {
//                                Icon(
//                                    painter = painterResource(if (menuExpanded) R.drawable.menu_open_24px else R.drawable.menu_24px),
//                                    contentDescription = "用户菜单",
//                                )
//                            }

                // 3. 高颜值定制化 DropdownMenu
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    // 通过 offset 让菜单向下微调，避免死死贴着顶栏，视觉上更轻盈
                    offset = DpOffset(x = (-8).dp, y = 4.dp),
                    modifier = Modifier.width(170.dp),
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp,
                ) {
                    // 菜单项 1：登出
                    DropdownMenuItem(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        text = {
                            Text(
                                "登出",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.face_24px),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        onClick = {
                            menuExpanded = false // 点击后关闭
                        }
                    )

                    // 分割线：增强视觉层次
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    // 菜单项 2：其他设置（示例）
                    DropdownMenuItem(
                        modifier = Modifier.clip(RoundedCornerShape(16.dp)),
                        text = {
                            Text(
                                "设置",
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.face_24px),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            // 在这里处理设置点击
                        }
                    )
                }
            }
        }
    )
}
