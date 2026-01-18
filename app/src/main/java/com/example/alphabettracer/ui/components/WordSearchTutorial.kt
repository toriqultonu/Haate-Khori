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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WordSearchTutorial(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val tutorialSteps = listOf(
        TutorialStep(
            emoji = "🔍",
            title = "Find Hidden Words!",
            description = "Words are hidden in the grid below",
            handDirection = HandDirection.NONE
        ),
        TutorialStep(
            emoji = "👆",
            title = "Touch & Drag",
            description = "Touch a letter and drag across to select a word",
            handDirection = HandDirection.HORIZONTAL
        ),
        TutorialStep(
            emoji = "↗️",
            title = "Any Direction!",
            description = "Words can go horizontal, vertical, or diagonal",
            handDirection = HandDirection.DIAGONAL
        ),
        TutorialStep(
            emoji = "🎯",
            title = "You're Ready!",
            description = "Find all the words to win. Have fun!",
            handDirection = HandDirection.NONE
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
                .background(Color.Black.copy(alpha = 0.4f))
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
                InstructionCard(
                    step = tutorialSteps[currentStep],
                    currentStepIndex = currentStep,
                    totalSteps = tutorialSteps.size,
                    onSkip = onSkip
                )
            }

            // Animated hand pointer on the grid area
            if (tutorialSteps[currentStep].handDirection != HandDirection.NONE) {
                AnimatedHandPointer(
                    direction = tutorialSteps[currentStep].handDirection,
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
                TapToContinueHint(
                    isLastStep = currentStep == tutorialSteps.size - 1,
                    onGotIt = onDismiss
                )
            }
        }
    }
}

@Composable
private fun InstructionCard(
    step: TutorialStep,
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
private fun AnimatedHandPointer(
    direction: HandDirection,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hand")

    // Animation progress (0 to 1)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "handProgress"
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

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val trailLength = 150.dp

    // Start and end positions for the trail
    val (startX, startY, endX, endY) = when (direction) {
        HandDirection.HORIZONTAL -> {
            listOf(-trailLength / 2, 20.dp, trailLength / 2, 20.dp)
        }
        HandDirection.DIAGONAL -> {
            listOf(-trailLength / 2, (-trailLength / 2) + 20.dp, trailLength / 2, (trailLength / 2) + 20.dp)
        }
        HandDirection.VERTICAL -> {
            listOf(0.dp, -trailLength / 2 + 20.dp, 0.dp, trailLength / 2 + 20.dp)
        }
        HandDirection.NONE -> listOf(0.dp, 0.dp, 0.dp, 0.dp)
    }

    // Current hand position based on progress
    val currentX = startX + (endX - startX) * progress
    val currentY = startY + (endY - startY) * progress

    Box(
        modifier = modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw selection trail using Canvas
        if (progress > 0.1f) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                val lineStartX = centerX + startX.toPx()
                val lineStartY = centerY + startY.toPx()
                val lineEndX = centerX + startX.toPx() + (endX.toPx() - startX.toPx()) * progress
                val lineEndY = centerY + startY.toPx() + (endY.toPx() - startY.toPx()) * progress

                drawLine(
                    color = Color(0xFF6200EE).copy(alpha = 0.5f),
                    start = Offset(lineStartX, lineStartY),
                    end = Offset(lineEndX, lineEndY),
                    strokeWidth = 40.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Hand emoji
        Text(
            text = "👆",
            fontSize = 50.sp,
            modifier = Modifier
                .offset(x = currentX, y = currentY)
                .scale(scale)
        )
    }
}

@Composable
private fun TapToContinueHint(
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
                text = "Let's Play!",
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

private data class TutorialStep(
    val emoji: String,
    val title: String,
    val description: String,
    val handDirection: HandDirection
)

private enum class HandDirection {
    NONE,
    HORIZONTAL,
    VERTICAL,
    DIAGONAL
}
