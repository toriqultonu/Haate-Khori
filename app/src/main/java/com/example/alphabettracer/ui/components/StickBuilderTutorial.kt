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

    // Animation progress (0 to 1) - moving from bottom to top
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "dragProgress"
    )

    // Pulsing effect for the hand
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handScale"
    )

    val startY = 120.dp
    val endY = (-80).dp
    val currentY = startY + (endY - startY) * progress

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw a stick being dragged
        if (progress > 0.1f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val centerX = size.width / 2
                val stickStartY = size.height * 0.7f
                val stickCurrentY = stickStartY + (size.height * 0.2f - stickStartY) * progress

                // Draw the stick (horizontal)
                drawRoundRect(
                    color = Color(0xFFE57373), // Red stick
                    topLeft = Offset(centerX - 60f, stickCurrentY - 20f),
                    size = Size(120f, 40f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // Draw trail
                drawLine(
                    color = Color(0xFF8B4513).copy(alpha = 0.3f),
                    start = Offset(centerX, stickStartY),
                    end = Offset(centerX, stickCurrentY),
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Hand emoji following the stick
        Text(
            text = "👆",
            fontSize = 50.sp,
            modifier = Modifier
                .offset(y = currentY)
                .scale(scale)
        )
    }
}

@Composable
private fun DropOnSegmentAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "drop")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing, delayMillis = 200),
            repeatMode = RepeatMode.Restart
        ),
        label = "dropProgress"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dropScale"
    )

    Box(
        modifier = modifier.size(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Draw a simplified 7-segment outline (just top segment highlighted)
            val segmentWidth = 100f
            val segmentHeight = 25f

            // Ghost segment (where to drop)
            val targetY = centerY - 60f

            // Pulsing highlight for target segment
            val highlightAlpha = 0.3f + 0.3f * progress
            drawRoundRect(
                color = Color(0xFF4CAF50).copy(alpha = highlightAlpha),
                topLeft = Offset(centerX - segmentWidth / 2, targetY - segmentHeight / 2),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // Stick moving toward segment
            val stickStartY = centerY + 80f
            val stickEndY = targetY
            val stickCurrentY = stickStartY + (stickEndY - stickStartY) * progress

            drawRoundRect(
                color = Color(0xFF64B5F6), // Blue stick
                topLeft = Offset(centerX - segmentWidth / 2, stickCurrentY - segmentHeight / 2),
                size = Size(segmentWidth, segmentHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }

        // Hand pointer
        val handY = 80.dp + ((-60).dp - 80.dp) * progress
        Text(
            text = "👆",
            fontSize = 45.sp,
            modifier = Modifier
                .offset(y = handY)
                .scale(scale)
        )
    }
}

@Composable
private fun PointToHintAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "point")

    // Pointing animation (slight movement toward hint button)
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointX"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pointScale"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw hint button representation
        Canvas(modifier = Modifier.fillMaxSize()) {
            val buttonCenterX = size.width * 0.3f
            val buttonCenterY = size.height * 0.4f
            val buttonRadius = 35f

            // Button background (yellow/orange like hint button)
            drawCircle(
                color = Color(0xFFFFB74D),
                radius = buttonRadius,
                center = Offset(buttonCenterX, buttonCenterY)
            )

            // Pulsing glow effect
            drawCircle(
                color = Color(0xFFFFB74D).copy(alpha = 0.3f),
                radius = buttonRadius + 15f,
                center = Offset(buttonCenterX, buttonCenterY)
            )
        }

        // Lightbulb emoji on the button
        Text(
            text = "💡",
            fontSize = 28.sp,
            modifier = Modifier.offset(x = (-35).dp, y = (-20).dp)
        )

        // Pointing hand
        Text(
            text = "👈",
            fontSize = 50.sp,
            modifier = Modifier
                .offset(x = (30 + offsetX).dp, y = (-20).dp)
                .scale(scale)
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
