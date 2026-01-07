package com.example.alphabettracer.ui.components

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
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,          // Horizontal position (0-1)
    val speed: Float,      // Fall speed multiplier
    val size: Float,       // Size of particle
    val color: Color,      // Color
    val rotation: Float,   // Initial rotation
    val rotationSpeed: Float, // Rotation speed
    val wobble: Float,     // Horizontal wobble amount
    val wobbleSpeed: Float // Wobble frequency
)

@Composable
fun ConfettiAnimation(
    isPlaying: Boolean,
    onAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val confettiColors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFFFFE66D), // Yellow
        Color(0xFF95E1D3), // Mint
        Color(0xFFF38181), // Coral
        Color(0xFFAA96DA), // Purple
        Color(0xFFFFAA5C), // Orange
        Color(0xFF88D8B0), // Green
        Color(0xFFFF9FF3), // Pink
        Color(0xFF54A0FF)  // Blue
    )

    val particles = remember {
        List(80) {
            ConfettiParticle(
                x = Random.nextFloat(),
                speed = 0.5f + Random.nextFloat() * 0.8f,
                size = 8f + Random.nextFloat() * 12f,
                color = confettiColors.random(),
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = -200f + Random.nextFloat() * 400f,
                wobble = 20f + Random.nextFloat() * 40f,
                wobbleSpeed = 2f + Random.nextFloat() * 4f
            )
        }
    }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
            )
            onAnimationEnd()
        }
    }

    if (isPlaying || progress.value > 0f && progress.value < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            particles.forEach { particle ->
                val animProgress = progress.value

                // Start from above the screen
                val startY = -50f
                val endY = canvasHeight + 100f
                val y = startY + (endY - startY) * animProgress * particle.speed

                // Only draw if particle is in visible range
                if (y > -50f && y < canvasHeight + 50f) {
                    // Horizontal wobble
                    val wobbleOffset = sin(animProgress * particle.wobbleSpeed * Math.PI.toFloat() * 2) * particle.wobble
                    val x = particle.x * canvasWidth + wobbleOffset

                    // Rotation
                    val rotation = particle.rotation + animProgress * particle.rotationSpeed

                    rotate(rotation, pivot = Offset(x, y)) {
                        // Draw confetti as small rectangles
                        drawRect(
                            color = particle.color,
                            topLeft = Offset(x - particle.size / 2, y - particle.size / 4),
                            size = Size(particle.size, particle.size / 2)
                        )
                    }
                }
            }
        }
    }
}
