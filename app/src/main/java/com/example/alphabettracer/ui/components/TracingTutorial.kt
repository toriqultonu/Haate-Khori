package com.example.alphabettracer.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TracingTutorial(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val tutorialSteps = listOf(
        TracingTutorialStep(
            emoji = "✏️",
            title = "Let's Learn to Trace!",
            description = "Practice writing letters by tracing with your finger",
            animationType = TracingAnimationType.NONE
        ),
        TracingTutorialStep(
            emoji = "👆",
            title = "Trace the Letter",
            description = "Follow the guide dots to draw the letter",
            animationType = TracingAnimationType.TRACE_LETTER
        ),
        TracingTutorialStep(
            emoji = "🎨",
            title = "Pick Your Colors",
            description = "Tap the color button to change your pen color",
            animationType = TracingAnimationType.TAP_COLOR
        ),
        TracingTutorialStep(
            emoji = "✅",
            title = "Check Your Work",
            description = "Tap the check button to see how well you traced",
            animationType = TracingAnimationType.TAP_CHECK
        ),
        TracingTutorialStep(
            emoji = "🌟",
            title = "You're Ready!",
            description = "Get stars by tracing accurately. Have fun!",
            animationType = TracingAnimationType.NONE
        )
    )

    // Auto-advance through steps
    LaunchedEffect(currentStep, isVisible) {
        if (isVisible && currentStep < tutorialSteps.size - 1) {
            delay(3500)
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
                TracingInstructionCard(
                    step = tutorialSteps[currentStep],
                    currentStepIndex = currentStep,
                    totalSteps = tutorialSteps.size,
                    onSkip = onSkip
                )
            }

            // Animated hand pointer
            if (tutorialSteps[currentStep].animationType != TracingAnimationType.NONE) {
                TracingAnimatedHand(
                    animationType = tutorialSteps[currentStep].animationType,
                    modifier = Modifier.align(Alignment.Center)
                )
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
                TracingTapToContinueHint(
                    isLastStep = currentStep == tutorialSteps.size - 1,
                    onGotIt = onDismiss
                )
            }
        }
    }
}

@Composable
private fun TracingInstructionCard(
    step: TracingTutorialStep,
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
                                    color = if (index == currentStepIndex)
                                        Color(0xFF6200EE)
                                    else if (index < currentStepIndex)
                                        Color(0xFF4CAF50)
                                    else
                                        Color.LightGray,
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
                color = Color(0xFF6200EE),
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
private fun TracingAnimatedHand(
    animationType: TracingAnimationType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "tracingHand")

    // Animation progress (0 to 1)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing, delayMillis = 400),
            repeatMode = RepeatMode.Restart
        ),
        label = "tracingProgress"
    )

    // Pulsing effect for the hand
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tracingScale"
    )

    // Tapping animation for buttons
    val tapScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, delayMillis = 800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tapScale"
    )

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            TracingAnimationType.TRACE_LETTER -> {
                // Draw an "A" shape path that the hand follows
                TracingLetterAnimation(progress = progress, scale = scale)
            }
            TracingAnimationType.TAP_COLOR -> {
                // Hand tapping a color circle
                TapButtonAnimation(
                    progress = progress,
                    scale = tapScale,
                    buttonEmoji = "🎨",
                    buttonColor = Color(0xFFE91E63)
                )
            }
            TracingAnimationType.TAP_CHECK -> {
                // Hand tapping check button
                TapButtonAnimation(
                    progress = progress,
                    scale = tapScale,
                    buttonEmoji = "✓",
                    buttonColor = Color(0xFF4CAF50)
                )
            }
            TracingAnimationType.NONE -> {
                // No animation
            }
        }
    }
}

@Composable
private fun TracingLetterAnimation(
    progress: Float,
    scale: Float
) {
    val strokeColor = Color(0xFF6200EE)

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw the tracing trail (letter "A" shape)
        Canvas(modifier = Modifier.size(150.dp)) {
            val width = size.width
            val height = size.height

            // Define "A" letter path points
            val points = listOf(
                Offset(width * 0.5f, height * 0.1f),   // Top
                Offset(width * 0.2f, height * 0.9f),   // Bottom left
                Offset(width * 0.35f, height * 0.55f), // Middle left
                Offset(width * 0.65f, height * 0.55f), // Middle right
                Offset(width * 0.8f, height * 0.9f),   // Bottom right
                Offset(width * 0.5f, height * 0.1f)    // Back to top
            )

            // Calculate how many points to draw based on progress
            val totalSegments = points.size - 1
            val currentSegment = (progress * totalSegments).toInt().coerceAtMost(totalSegments - 1)
            val segmentProgress = (progress * totalSegments) - currentSegment

            // Draw completed segments
            if (progress > 0.05f) {
                val path = Path()
                path.moveTo(points[0].x, points[0].y)

                for (i in 0 until currentSegment) {
                    path.lineTo(points[i + 1].x, points[i + 1].y)
                }

                // Draw current segment partially
                if (currentSegment < totalSegments) {
                    val startPoint = points[currentSegment]
                    val endPoint = points[currentSegment + 1]
                    val currentX = startPoint.x + (endPoint.x - startPoint.x) * segmentProgress
                    val currentY = startPoint.y + (endPoint.y - startPoint.y) * segmentProgress
                    path.lineTo(currentX, currentY)
                }

                drawPath(
                    path = path,
                    color = strokeColor.copy(alpha = 0.6f),
                    style = Stroke(
                        width = 12.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Hand emoji following the path
        val handOffsetX = when {
            progress < 0.2f -> {
                // Going from top to bottom left
                val p = progress / 0.2f
                (-60.dp * p)
            }
            progress < 0.4f -> {
                // Going to middle left
                val p = (progress - 0.2f) / 0.2f
                -60.dp + (30.dp * p)
            }
            progress < 0.6f -> {
                // Going across middle
                val p = (progress - 0.4f) / 0.2f
                -30.dp + (60.dp * p)
            }
            progress < 0.8f -> {
                // Going to bottom right
                val p = (progress - 0.6f) / 0.2f
                30.dp + (30.dp * p)
            }
            else -> {
                // Going back to top
                val p = (progress - 0.8f) / 0.2f
                60.dp - (60.dp * p)
            }
        }

        val handOffsetY = when {
            progress < 0.2f -> {
                // Going from top to bottom left
                val p = progress / 0.2f
                -60.dp + (120.dp * p)
            }
            progress < 0.4f -> {
                // Going to middle left
                val p = (progress - 0.2f) / 0.2f
                60.dp - (50.dp * p)
            }
            progress < 0.6f -> {
                // Going across middle
                10.dp
            }
            progress < 0.8f -> {
                // Going to bottom right
                val p = (progress - 0.6f) / 0.2f
                10.dp + (50.dp * p)
            }
            else -> {
                // Going back to top
                val p = (progress - 0.8f) / 0.2f
                60.dp - (120.dp * p)
            }
        }

        Text(
            text = "👆",
            fontSize = 44.sp,
            modifier = Modifier
                .offset(x = handOffsetX, y = handOffsetY)
                .scale(scale)
        )
    }
}

@Composable
private fun TapButtonAnimation(
    progress: Float,
    scale: Float,
    buttonEmoji: String,
    buttonColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "buttonPulse")

    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulseScale"
    )

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        // Button circle
        Box(
            modifier = Modifier
                .size(80.dp)
                .scale(buttonScale)
                .background(
                    color = buttonColor.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = buttonColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonEmoji,
                    fontSize = 28.sp,
                    color = Color.White
                )
            }
        }

        // Hand tapping
        Text(
            text = "👆",
            fontSize = 44.sp,
            modifier = Modifier
                .offset(x = 30.dp, y = 50.dp)
                .scale(scale)
        )
    }
}

@Composable
private fun TracingTapToContinueHint(
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
                containerColor = Color(0xFF4CAF50)
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Let's Start!",
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

private data class TracingTutorialStep(
    val emoji: String,
    val title: String,
    val description: String,
    val animationType: TracingAnimationType
)

private enum class TracingAnimationType {
    NONE,
    TRACE_LETTER,
    TAP_COLOR,
    TAP_CHECK
}
