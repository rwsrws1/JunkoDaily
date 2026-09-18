package com.example.learncompose.feature.routine.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// Single particle data structure
private data class Particle(
    val angle: Double,
    val speed: Float,
    val maxDistance: Float,
    val size: Float,
    val color: Color,
    val rotationSpeed: Float,
    val isCircle: Boolean
)

@Composable
fun ExplosionConfetti(
    modifier: Modifier = Modifier,
    primaryColors: List<Color> = listOf(
        Color(0xFFFF5722),
        Color(0xFFFFEB3B),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFE91E63)
    ),
    particleCount: Int = 80,
    maxRadius: Float = 400f, // Max explosion spread radius in px
    durationMillis: Int = 1200,
    onAnimationEnd: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    // Generate random particles centered at launch
    val particles = remember(primaryColors, particleCount, maxRadius) {
        List(particleCount) {
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = Random.nextFloat() * 0.4f + 0.6f // Variation in speed distance
            Particle(
                angle = angle,
                speed = speed,
                maxDistance = maxRadius * speed,
                size = Random.nextFloat() * 12f + 8f,
                color = primaryColors.random(),
                rotationSpeed = Random.nextFloat() * 720f - 360f,
                isCircle = Random.nextBoolean()
            )
        }
    }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )
        onAnimationEnd()
    }

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val t = progress.value

        // Fade out during the second half of the animation
        val alpha = if (t > 0.5f) (1f - (t - 0.5f) / 0.5f) else 1f

        particles.forEach { p ->
            // Apply simple gravity/drag factor over time
            val distance = p.maxDistance * (1f - (1f - t) * (1f - t)) // Ease-out quad
            val currentX = center.x + (distance * cos(p.angle)).toFloat()
            // Add vertical drop to simulate gravity
            val currentY = center.y + (distance * sin(p.angle)).toFloat() + (t * t * 150f)

            val currentRotation = p.rotationSpeed * t

            withTransform({
                translate(left = currentX, top = currentY)
                rotate(degrees = currentRotation, pivot = Offset.Zero)
            }) {
                if (p.isCircle) {
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size / 2,
                        center = Offset.Zero
                    )
                } else {
                    drawRect(
                        color = p.color.copy(alpha = alpha),
                        topLeft = Offset(-p.size / 2, -p.size / 2),
                        size = Size(p.size, p.size * 0.6f)
                    )
                }
            }
        }
    }
}