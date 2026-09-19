package com.example.learncompose.feature.routine.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import com.example.learncompose.core.designsystem.property.PresetImage
import com.example.learncompose.feature.routine.R
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun ImageAreaCard(
    modifier: Modifier = Modifier,
    targetShape: RoundedPolygon,
    imageColor: Color,
    selectImage: String = PresetImage.defaultImage.resName,
    onImageClick: (() -> Unit)?
) {
    val morphProgress = remember { Animatable(0f) }
    val rotationProgress = remember { Animatable(0f) }
    var currentShape by remember { mutableStateOf(targetShape) }

    val morph = remember(currentShape, targetShape) { Morph(currentShape.normalized(), targetShape.normalized()) }

    val path = remember { Path() }
    val scaleMatrix = remember { Matrix() }

    LaunchedEffect(targetShape) {
        val morphAnimationSpec = spring<Float>(dampingRatio = 0.6f, stiffness = 200f)
        launch {
            val animationResult = morphProgress.animateTo(
                targetValue = 1f,
                animationSpec = morphAnimationSpec
            )
            if (animationResult.endReason == AnimationEndReason.Finished) {
                currentShape = targetShape
                morphProgress.snapTo(0f)
            }
        }
//        launch {
//            val animationResult = rotationProgress.animateTo(
//                targetValue = if (changeShape) 1f / 4f else 0f,
//                animationSpec = morphAnimationSpec
//            )
//            if (animationResult.endReason == AnimationEndReason.Finished) {
//            }
//        }
    }

    var targetSize by remember { mutableStateOf(Size.Zero) }
    val contentFill = 0.9f

// 主卡片容器
    Box(
        modifier = modifier
    ) {
        // 1. 底层：绘制动态 Shape 背景
        Box(
            modifier = Modifier
                .fillMaxSize(contentFill)
                .onGloballyPositioned { coordinates ->
                    targetSize = coordinates.size.toSize() // 将尺寸存入状态
                }
                .align(Alignment.Center)
                .drawWithCache {
                    onDrawBehind {
                        val progress = morphProgress.value
                        val rotProgress = rotationProgress.value

                        rotate(rotProgress) {
                            drawPath(
                                path = processPath(
                                    path = morph.toPath(
                                        progress = progress,
                                        path = path,
                                        startAngle = 0,
                                    ),
                                    size = size,
                                    scaleFactor = 1.0f,
                                    scaleMatrix = scaleMatrix,
                                ),
                                color = imageColor,
                                style = Fill,
                            )
                        }
                    }
                }
                .then(
                    if (onImageClick != null) {
                        Modifier.clickable(
                            onClick = onImageClick,
                            interactionSource = null,
                            indication = null
                        )
                    } else {
                        Modifier
                    }
                )
        )

        // 2. 限制层：底部与两侧被动态 Shape 严格裁剪的图片部分
        Image(
            painter = painterResource(PresetImage.fromResName(selectImage).resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(contentFill)
                .align(Alignment.Center)

                .graphicsLayer {
                    // 使用图形图层和混合模式，将裁剪范围严格限制在底部及两侧
                    clip = true
                    shape = object : Shape {
                        override fun createOutline(
                            size: Size,
                            layoutDirection: LayoutDirection,
                            density: Density
                        ): Outline {
                            // 创建动态的 Morph Outline
                            val morphedPath = processPath(
                                path = morph.toPath(
                                    progress = morphProgress.value,
                                    path = path,
                                    startAngle = 0
                                ),
                                size = size,
                                scaleFactor = 1.0f,
                                scaleMatrix = scaleMatrix
                            )
                            return Outline.Generic(morphedPath)
                        }
                    }
                }
                .drawWithContent {
                    // 利用 Canvas 裁切：只绘制顶部 0~100% 以外（即中下区域）的内容，避免与顶层重叠
                    drawContent()
                }
                .scale(1f)
        )

        // 3. 溢出层：顶部不受 Shape 约束、允许透出的图片部分
        Image(
            painter = painterResource(PresetImage.fromResName(selectImage).resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize(contentFill)
                .align(Alignment.Center)

                .drawWithContent {
                    // 仅绘制顶部超出/上半部分区域，避免底部溢出
                    clipRect(
                        left = -size.width,
                        top = -size.height, // 允许顶部往上无限延伸绘制
                        right = size.width * 2,
                        bottom = size.height * 0.5f // 裁剪掉下半部分，交给受控的底层绘制
                    ) {
                        this@drawWithContent.drawContent()
                    }
                }
                .scale(1f)
        )
    }
}

private fun processPath(
    path: Path,
    size: Size,
    scaleFactor: Float,
    scaleMatrix: Matrix = Matrix(),
): Path {
    scaleMatrix.reset()
    scaleMatrix.apply { scale(x = size.width * scaleFactor, y = size.height * scaleFactor) }
    path.transform(scaleMatrix)
    path.translate(size.center - path.getBounds().center)
    return path
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun Preview() {
    ImageAreaCard(modifier = Modifier.size(300.dp), targetShape = MaterialShapes.Heart,
        imageColor = MaterialTheme.colorScheme.tertiary, onImageClick = null)
}