package com.example.learncompose.core.designsystem.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.launch

@Composable
fun TwistMorphCard(
    aspectRatio: Float = 2f / 3f,
    frontFaceColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    backFaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    frontFaceContent: @Composable ColumnScope.() -> Unit = {},
    backFaceContent: @Composable ColumnScope.() -> Unit = {}
) {
    var isFront by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    val scaleX = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(1f) }
    val rotationZ = rememberSaveable(saver = FloatAnimatableSaver) { Animatable(0f) }

    val handleTwist = {
        scope.launch {
            // 1. 横向挤压至极限并轻微旋转
            launch {
                rotationZ.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                )
            }
            scaleX.animateTo(
                targetValue = 0.05f,
                animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
            )

            // 2. 变换内容
            isFront = !isFront

            // 3. 展开归位
            launch {
                rotationZ.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing)
                )
            }
            scaleX.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 260, easing = LinearOutSlowInEasing)
            )
        }
    }

    Card(
        modifier = Modifier
            .aspectRatio(aspectRatio)
            .graphicsLayer {
                this.scaleX = scaleX.value
                this.rotationZ = rotationZ.value
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!scaleX.isRunning) handleTwist()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isFront) frontFaceColor else backFaceColor
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isFront) frontFaceContent() else backFaceContent()
            }
        }
    }
}