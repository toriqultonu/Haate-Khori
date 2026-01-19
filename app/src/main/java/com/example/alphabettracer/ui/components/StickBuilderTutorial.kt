package com.example.alphabettracer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun StickBuilderTutorial(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val tutorialSteps = listOf(
        StickTutorialStep(
            emoji = "🪵",
            title = "Build with Sticks!",
            description = "Drag sticks to build numbers like a calculator display",
            animationType = StickAnimationType.NONE
        ),
        StickTutorialStep(
            emoji = "👆",
            title = "Drag from Tray",
            description = "Touch a stick at the bottom and drag it up to the board",
            animationType = StickAnimationType.DRAG_UP
        ),
        StickTutorialStep(
            emoji = "🎯",
            title = "Drop on Segment",
            description = "Release the stick on the glowing segment to place it",
            animationType = StickAnimationType.DROP_ON_SEGMENT
        ),
        StickTutorialStep(
            emoji = "💡",
            title = "Need Help?",
            description = "Tap the lightbulb button to see which segments to fill",
            animationType = StickAnimationType.POINT_HINT
        ),
        StickTutorialStep(
            emoji = "✅",
            title = "You're Ready!",
            description = "Build the pattern and tap CHECK to win!",
            animationType = StickAnimationType.NONE
        )
    )

    // Auto-advance through steps
    LaunchedEffect(currentStep, isVisible) {
        if (isVisible && currentStep < tutorialSteps.size - 1) {
            delay(4000) // Slightly longer for StickBuilder as it's more complex
            currentStep++
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    // Tap anywhere to advance or dismiss
                    if (currentStep < tutorialSteps.size - 1) {
                        currentStep++
                    } else {
                        onDismiss()
                    }
                }
        ) {
            // Top instruction card
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                StickInstructionCard(
                    step = tutorialSteps[currentStep],
                    currentStepIndex = currentStep,
                    totalSteps = tutorialSteps.size,
                    onSkip = onSkip
                )
            }

            // Animated hand pointer based on step type
            when (tutorialSteps[currentStep].animationType) {
                StickAnimationType.DRAG_UP -> {
                    DragUpAnimation(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                StickAnimationType.DROP_ON_SEGMENT -> {
                    DropOnSegmentAnimation(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                StickAnimationType.POINT_HINT -> {
                    PointToHintAnimation(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                StickAnimationType.NONE -> { /* No animation */ }
            }

            // Bottom hint
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
            ) {
                StickTapToContinueHint(
                    isLastStep = currentStep == tutorialSteps.size - 1,
                    onGotIt = onDismiss
                )
            }
        }
    }
}

@Composable
private fun StickInstructionCard(
    step: StickTutorialStep,
    currentStepIndex: Int,
    totalSteps: Int,
    onSkip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step indicator dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    repeat(totalSteps) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == currentStepIndex) 10.dp else 8.dp)
                                .background(
                                    color = when {
                                        index == currentStepIndex -> Color(0xFF8B4513) // Brown for current
                                        index < currentStepIndex -> Color(0xFF4CAF50) // Green for completed
                                        else -> Color.LightGray
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }

                // Skip button
                IconButton(
                    onClick = onSkip,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Skip tutorial",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Emoji
            Text(
                text = step.emoji,
                fontSize = 48.sp
            )

            Spacer(Modifier.height(8.dp))

            // Title
            Text(
                text = step.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B4513), // Brown theme for StickBuilder
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(4.dp))

            // Description
            Text(
                text = step.description,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun DragUpAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dragUp")

    // Main drag progress with smooth easing
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutCubic, delayMillis = 400),
            repeatMode = RepeatMode.Restart
        ),
        label = "dragProgress"
    )

    // Gentle pulsing effect for the hand
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handPulse"
    )

    // Subtle rotation during drag for natural feel
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handRotation"
    )

    // Hand press-down effect at start
    val pressScale = when {
        progress < 0.1f -> 1f - (progress * 2f) // Press down
        progress < 0.2f -> 0.8f + ((progress - 0.1f) * 2f) // Release
        else -> 1f
    }

    val startY = 130.dp
    val endY = (-90).dp
    val currentY = startY + (endY - startY) * progress

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val stickStartY = size.height * 0.75f
            val stickEndY = size.height * 0.18f
            val stickCurrentY = stickStartY + (stickEndY - stickStartY) * progress

            // Draw tray at bottom
            drawRoundRect(
                color = Color(0xFFD7CCC8),
                topLeft = Offset(centerX - 100f, size.height * 0.7f),
                size = Size(200f, 60f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = Color(0xFFBCAAA4),
                topLeft = Offset(centerX - 90f, size.height * 0.72f),
                size = Size(180f, 45f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Draw fading trail with gradient
            if (progress > 0.15f) {
                val trailAlpha = (0.5f * (1f - progress * 0.5f)).coerceIn(0f, 0.5f)
                for (i in 0..4) {
                    val trailY = stickStartY + (stickCurrentY - stickStartY) * (i / 5f)
                    val alpha = trailAlpha * (1f - i / 5f)
                    drawRoundRect(
                        color = Color(0xFFE57373).copy(alpha = alpha),
                        topLeft = Offset(centerX - 55f, trailY - 18f),
                        size = Size(110f, 36f),
                        cornerRadius = CornerRadius(18f, 18f)
                    )
                }
            }

            // Draw stick shadow
            if (progress > 0.05f) {
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.2f),
                    topLeft = Offset(centerX - 53f + 4f, stickCurrentY - 16f + 4f),
                    size = Size(106f, 32f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
            }

            // Draw the main stick with 3D effect
            if (progress > 0.05f) {
                // Main stick body
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFEF5350),
                            Color(0xFFE53935),
                            Color(0xFFC62828)
                        )
                    ),
                    topLeft = Offset(centerX - 55f, stickCurrentY - 18f),
                    size = Size(110f, 36f),
                    cornerRadius = CornerRadius(18f, 18f)
                )

                // Highlight on stick
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(centerX - 50f, stickCurrentY - 14f),
                    size = Size(100f, 12f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }

            // Draw target zone indicator at top
            val targetAlpha = (0.3f + 0.2f * kotlin.math.sin(progress * 6f)).coerceIn(0.2f, 0.5f)
            drawRoundRect(
                color = Color(0xFF4CAF50).copy(alpha = targetAlpha),
                topLeft = Offset(centerX - 60f, stickEndY - 22f),
                size = Size(120f, 44f),
                cornerRadius = CornerRadius(22f, 22f),
                style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f)))
            )
        }

        // Hand emoji with natural movement
        Text(
            text = "👆",
            fontSize = 52.sp,
            modifier = Modifier
                .offset(x = 8.dp, y = currentY)
                .scale(pulse * pressScale)
                .rotate(rotation * (1f - progress * 0.5f))
        )
    }
}

@Composable
private fun DropOnSegmentAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "drop")

    // Smooth easing for drop motion
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutCubic, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropProgress"
    )

    // Gentle pulse for hand
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dropPulse"
    )

    // Glow animation for target
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Bounce effect when stick lands
    val bounceScale = when {
        progress > 0.9f -> 1f + (1f - progress) * 2f * kotlin.math.sin((progress - 0.9f) * 30f)
        else -> 1f
    }

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            val segmentWidth = 120f
            val segmentHeight = 32f

            // Target segment position
            val targetY = centerY - 50f

            // Draw 7-segment ghost outline (simplified - just showing a few segments)
            val ghostAlpha = 0.25f
            // Top segment (target)
            drawRoundRect(
                color = Color.Gray.copy(alpha = ghostAlpha),
                topLeft = Offset(centerX - segmentWidth / 2, targetY - segmentHeight / 2),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
            )
            // Left vertical segment
            drawRoundRect(
                color = Color.Gray.copy(alpha = ghostAlpha),
                topLeft = Offset(centerX - segmentWidth / 2 - 10f, targetY + segmentHeight),
                size = Size(segmentHeight, segmentWidth * 0.8f),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
            )
            // Right vertical segment
            drawRoundRect(
                color = Color.Gray.copy(alpha = ghostAlpha),
                topLeft = Offset(centerX + segmentWidth / 2 - 22f, targetY + segmentHeight),
                size = Size(segmentHeight, segmentWidth * 0.8f),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))
            )

            // Glowing target segment highlight
            val glowRadius = 8f + glowPulse * 12f
            val glowAlpha = 0.2f + glowPulse * 0.25f
            drawRoundRect(
                color = Color(0xFF4CAF50).copy(alpha = glowAlpha),
                topLeft = Offset(centerX - segmentWidth / 2 - glowRadius, targetY - segmentHeight / 2 - glowRadius),
                size = Size(segmentWidth + glowRadius * 2, segmentHeight + glowRadius * 2),
                cornerRadius = CornerRadius(16f + glowRadius, 16f + glowRadius)
            )

            // Target segment border
            drawRoundRect(
                color = Color(0xFF4CAF50).copy(alpha = 0.6f + glowPulse * 0.4f),
                topLeft = Offset(centerX - segmentWidth / 2, targetY - segmentHeight / 2),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 3f)
            )

            // Stick movement
            val stickStartY = centerY + 90f
            val stickEndY = targetY
            val stickCurrentY = stickStartY + (stickEndY - stickStartY) * progress

            // Stick shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.15f),
                topLeft = Offset(centerX - segmentWidth / 2 + 4f, stickCurrentY - segmentHeight / 2 + 4f),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Main stick with gradient
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF64B5F6),
                        Color(0xFF42A5F5),
                        Color(0xFF1E88E5)
                    )
                ),
                topLeft = Offset(
                    centerX - segmentWidth / 2,
                    stickCurrentY - segmentHeight / 2
                ),
                size = Size(segmentWidth * bounceScale, segmentHeight * bounceScale),
                cornerRadius = CornerRadius(16f, 16f)
            )

            // Stick highlight
            drawRoundRect(
                color = Color.White.copy(alpha = 0.35f),
                topLeft = Offset(centerX - segmentWidth / 2 + 6f, stickCurrentY - segmentHeight / 2 + 4f),
                size = Size(segmentWidth - 12f, 10f),
                cornerRadius = CornerRadius(5f, 5f)
            )

            // Success sparkles when dropped
            if (progress > 0.85f) {
                val sparkleAlpha = ((progress - 0.85f) / 0.15f).coerceIn(0f, 1f)
                val sparkleColors = listOf(Color(0xFFFFD700), Color(0xFF4CAF50), Color(0xFFE91E63))
                for (i in 0..5) {
                    val angle = i * 60f + progress * 180f
                    val distance = 30f + (progress - 0.85f) * 200f
                    val sparkleX = centerX + distance * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                    val sparkleY = targetY + distance * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                    drawCircle(
                        color = sparkleColors[i % 3].copy(alpha = sparkleAlpha * (1f - (progress - 0.85f) / 0.15f)),
                        radius = 6f,
                        center = Offset(sparkleX, sparkleY)
                    )
                }
            }
        }

        // Hand pointer with smooth movement
        val handY = 95.dp + ((-55).dp - 95.dp) * progress
        Text(
            text = "👆",
            fontSize = 48.sp,
            modifier = Modifier
                .offset(x = 10.dp, y = handY)
                .scale(pulse)
        )
    }
}

@Composable
private fun PointToHintAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "point")

    // Smooth pointing animation
    val pointProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointProgress"
    )

    // Button glow pulsing
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    // Hand wiggle for attention
    val wiggle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiggle"
    )

    val handOffsetX = -20f * pointProgress
    val handScale = 1f + 0.1f * pointProgress

    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val buttonCenterX = size.width * 0.32f
            val buttonCenterY = size.height * 0.42f
            val buttonRadius = 40f

            // Outer glow rings (multiple layers for nice effect)
            for (i in 3 downTo 1) {
                val ringRadius = buttonRadius + 15f * i + glowPulse * 8f
                val ringAlpha = 0.15f / i
                drawCircle(
                    color = Color(0xFFFFB74D).copy(alpha = ringAlpha),
                    radius = ringRadius,
                    center = Offset(buttonCenterX, buttonCenterY)
                )
            }

            // Button shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.2f),
                radius = buttonRadius,
                center = Offset(buttonCenterX + 3f, buttonCenterY + 3f)
            )

            // Button background with gradient effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFFFB74D),
                        Color(0xFFFFA726)
                    ),
                    center = Offset(buttonCenterX - 10f, buttonCenterY - 10f),
                    radius = buttonRadius * 1.5f
                ),
                radius = buttonRadius,
                center = Offset(buttonCenterX, buttonCenterY)
            )

            // Button highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.4f),
                radius = buttonRadius * 0.6f,
                center = Offset(buttonCenterX - 8f, buttonCenterY - 8f)
            )

            // Draw some hint segments to show what hint does
            val segmentColor = Color(0xFF4CAF50).copy(alpha = 0.4f + glowPulse * 0.3f)
            val segmentY = size.height * 0.7f
            val segmentCenterX = size.width * 0.5f

            // Top segment
            drawRoundRect(
                color = segmentColor,
                topLeft = Offset(segmentCenterX - 40f, segmentY - 50f),
                size = Size(80f, 20f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            // Left segment
            drawRoundRect(
                color = segmentColor,
                topLeft = Offset(segmentCenterX - 50f, segmentY - 25f),
                size = Size(20f, 60f),
                cornerRadius = CornerRadius(10f, 10f)
            )
            // Right segment
            drawRoundRect(
                color = segmentColor,
                topLeft = Offset(segmentCenterX + 30f, segmentY - 25f),
                size = Size(20f, 60f),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }

        // Lightbulb emoji on the button
        Text(
            text = "💡",
            fontSize = 30.sp,
            modifier = Modifier
                .offset(x = (-45).dp, y = (-18).dp)
                .scale(1f + glowPulse * 0.1f)
        )

        // Pointing hand with natural movement
        Text(
            text = "👈",
            fontSize = 52.sp,
            modifier = Modifier
                .offset(x = (35 + handOffsetX).dp, y = (-18 + wiggle * 0.3f).dp)
                .scale(handScale)
                .rotate(wiggle * 0.5f)
        )
    }
}

@Composable
private fun StickTapToContinueHint(
    isLastStep: Boolean,
    onGotIt: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    if (isLastStep) {
        Button(
            onClick = onGotIt,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513) // Brown theme
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Let's Build!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    } else {
        Text(
            text = "Tap anywhere to continue",
            fontSize = 14.sp,
            color = Color.White.copy(alpha = alpha),
            fontWeight = FontWeight.Medium
        )
    }
}

private data class StickTutorialStep(
    val emoji: String,
    val title: String,
    val description: String,
    val animationType: StickAnimationType
)

private enum class StickAnimationType {
    NONE,
    DRAG_UP,
    DROP_ON_SEGMENT,
    POINT_HINT
}
