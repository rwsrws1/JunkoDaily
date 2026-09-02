package com.example.learncompose.feature.experiment.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learncompose.feature.experiment.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CardToPageTransitionDemo() {
    // 控制当前显示的是列表卡片还是详情页
    var showDetails by remember { mutableStateOf(false) }

    // 1. 最外层包裹 SharedTransitionLayout
    SharedTransitionLayout {
        // 2. 使用 AnimatedContent 负责页面的切换动画
        AnimatedContent(
            targetState = showDetails,
            label = "card_to_page_transition"
        ) { isDetailsPage ->

            if (!isDetailsPage) {
                // =================【卡片视图 (起始状态)】=================
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            // 共享整个卡片的边界
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "card_bounds"),
                                animatedVisibilityScope = this@AnimatedContent,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(16.dp))
                            )
                            .clickable { showDetails = true } // 点击展开
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = R.drawable.insert_chart_24px_filled),
                                contentDescription = "Cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    // 共享图片元素
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "card_image"),
                                        animatedVisibilityScope = this@AnimatedContent
                                    )
                                    .background(Color.LightGray)
                            )
                            Text(
                                text = "Jetpack Compose 共享元素",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(16.dp)
                                    // 共享标题文本
                                    .sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "card_title"),
                                        animatedVisibilityScope = this@AnimatedContent
                                    )
                            )
                        }
                    }
                }
            } else {
                // =================【详情页视图 (目标状态)】=================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        // 共享边界的接收端，展开为全屏
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "card_bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(0.dp))
                        )
                        .clickable { showDetails = false } // 点击返回
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.insert_chart_24px_filled),
                        contentDescription = "Cover",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                            // 共享图片元素的接收端
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "card_image"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            .background(Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Jetpack Compose 共享元素",
                        fontSize = 28.sp, // 字体变大，系统会自动处理形变
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            // 共享标题文本的接收端
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(key = "card_title"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                    )
                    Text(
                        text = "这是详情页的正文内容。在这个页面中，图片从卡片的一部分拉伸成了详情页的顶部头图，标题文字也随之移动并放大了。",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    CardToPageTransitionDemo()
}