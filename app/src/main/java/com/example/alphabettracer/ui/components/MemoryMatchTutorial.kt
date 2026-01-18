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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MemoryMatchTutorial(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val tutorialSteps = listOf(
        MemoryTutorialStep(
            emoji = "🧠",
            title = "Memory Match!",
            description = "Train your brain by finding matching pairs",
            animationType = MemoryAnimationType.NONE
        ),
        MemoryTutorialStep(
            emoji = "👆",
            title = "Tap to Flip",
            description = "Tap any card to reveal what's hiding underneath",
            animationType = MemoryAnimationType.TAP_CARD
        ),
        MemoryTutorialStep(
            emoji = "🎯",
            title = "Find the Match",
            description = "Flip two cards - if they match, they stay revealed!",
            animationType = MemoryAnimationType.MATCH_CARDS
        ),
        MemoryTutorialStep(
            emoji = "🤔",
            title = "Remember Positions",
            description = "If cards don't match, remember where they were",
            animationType = MemoryAnimationType.REMEMBER
        ),
        MemoryTutorialStep(
            emoji = "🌟",
            title = "You're Ready!",
            description = "Match all pairs with fewer moves for more stars!",
            animationType = MemoryAnimationType.NONE
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
                MemoryInstructionCard(
                    step = tutorialSteps[currentStep],
                    currentStepIndex = currentStep,
                    totalSteps = tutorialSteps.size,
                    onSkip = onSkip
                )
            }

            // Animated hand pointer
            if (tutorialSteps[currentStep].animationType != MemoryAnimationType.NONE) {
                MemoryAnimatedHand(
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
                MemoryTapToContinueHint(
                    isLastStep = currentStep == tutorialSteps.size - 1,
                    onGotIt = onDismiss
                )
            }
        }
    }
}

@Composable
private fun MemoryInstructionCard(
    step: MemoryTutorialStep,
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
                                        Color(0xFF9C27B0)
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
                color = Color(0xFF9C27B0),
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
private fun MemoryAnimatedHand(
    animationType: MemoryAnimationType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "memoryHand")

    // Animation progress (0 to 1)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "memoryProgress"
    )

    // Pulsing effect for the hand
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "memoryScale"
    )

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            MemoryAnimationType.TAP_CARD -> {
                TapCardAnimation(progress = progress, scale = scale)
            }
            MemoryAnimationType.MATCH_CARDS -> {
                MatchCardsAnimation(progress = progress, scale = scale)
            }
            MemoryAnimationType.REMEMBER -> {
                RememberAnimation(progress = progress, scale = scale)
            }
            MemoryAnimationType.NONE -> {
                // No animation
            }
        }
    }
}

@Composable
private fun TapCardAnimation(
    progress: Float,
    scale: Float
) {
    val cardColor = Color(0xFF9C27B0)

    // Card flip animation based on progress
    val isFlipped = progress > 0.3f && progress < 0.8f
    val cardRotation = if (isFlipped) 180f else 0f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Single card
        Card(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    rotationY = if (progress > 0.3f) {
                        val flipProgress = ((progress - 0.3f) / 0.2f).coerceIn(0f, 1f)
                        flipProgress * 180f
                    } else 0f
                    cameraDistance = 12f * density
                },
            colors = CardDefaults.cardColors(
                containerColor = if (cardRotation > 90f) Color.White else cardColor
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (isFlipped) {
                    Text(
                        text = "🐱",
                        fontSize = 40.sp,
                        modifier = Modifier.graphicsLayer { rotationY = 180f }
                    )
                } else {
                    Text("❓", fontSize = 36.sp)
                }
            }
        }

        // Hand tapping
        val handOffsetY = if (progress < 0.3f) {
            // Moving towards card
            val p = progress / 0.3f
            80.dp - (30.dp * p)
        } else {
            // Tap and retreat
            50.dp
        }

        Text(
            text = "👆",
            fontSize = 44.sp,
            modifier = Modifier
                .offset(x = 30.dp, y = handOffsetY)
                .scale(if (progress > 0.25f && progress < 0.35f) 0.9f else scale)
        )
    }
}

@Composable
private fun MatchCardsAnimation(
    progress: Float,
    scale: Float
) {
    val cardColor = Color(0xFF9C27B0)
    val matchColor = Color(0xFF4CAF50)

    // Both cards flip and match
    val card1Flipped = progress > 0.2f
    val card2Flipped = progress > 0.5f
    val matched = progress > 0.75f

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First card
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        rotationY = if (card1Flipped) 180f else 0f
                        cameraDistance = 12f * density
                    }
                    .scale(if (matched) 1.1f else 1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (card1Flipped) {
                        if (matched) matchColor.copy(alpha = 0.3f) else Color.White
                    } else cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (card1Flipped) {
                        Text(
                            text = "🌟",
                            fontSize = 32.sp,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    } else {
                        Text("❓", fontSize = 28.sp)
                    }
                }
            }

            // Second card
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .graphicsLayer {
                        rotationY = if (card2Flipped) 180f else 0f
                        cameraDistance = 12f * density
                    }
                    .scale(if (matched) 1.1f else 1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (card2Flipped) {
                        if (matched) matchColor.copy(alpha = 0.3f) else Color.White
                    } else cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (card2Flipped) {
                        Text(
                            text = "🌟",
                            fontSize = 32.sp,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    } else {
                        Text("❓", fontSize = 28.sp)
                    }
                }
            }
        }

        // Match indicator
        if (matched) {
            Text(
                text = "✓",
                fontSize = 48.sp,
                color = matchColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(y = (-70).dp)
                    .scale(scale)
            )
        }

        // Hand pointing
        val handOffsetX = when {
            progress < 0.2f -> (-50).dp
            progress < 0.5f -> {
                val p = (progress - 0.2f) / 0.3f
                (-50).dp + (100.dp * p)
            }
            else -> 50.dp
        }

        if (!matched) {
            Text(
                text = "👆",
                fontSize = 40.sp,
                modifier = Modifier
                    .offset(x = handOffsetX, y = 60.dp)
                    .scale(scale)
            )
        }
    }
}

@Composable
private fun RememberAnimation(
    progress: Float,
    scale: Float
) {
    val cardColor = Color(0xFF9C27B0)

    // Cards flip then flip back
    val cardsFlipped = progress > 0.2f && progress < 0.6f
    val showThinking = progress > 0.65f

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // First card (cat)
            Card(
                modifier = Modifier
                    .size(70.dp)
                    .graphicsLayer {
                        rotationY = if (cardsFlipped) 180f else 0f
                        cameraDistance = 12f * density
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (cardsFlipped) Color.White else cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (cardsFlipped) {
                        Text(
                            text = "🐱",
                            fontSize = 28.sp,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    } else {
                        Text("❓", fontSize = 24.sp)
                    }
                }
            }

            // Second card (dog - different)
            Card(
                modifier = Modifier
                    .size(70.dp)
                    .graphicsLayer {
                        rotationY = if (cardsFlipped) 180f else 0f
                        cameraDistance = 12f * density
                    },
                colors = CardDefaults.cardColors(
                    containerColor = if (cardsFlipped) Color.White else cardColor
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (cardsFlipped) {
                        Text(
                            text = "🐕",
                            fontSize = 28.sp,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    } else {
                        Text("❓", fontSize = 24.sp)
                    }
                }
            }
        }

        // Thinking emoji when cards flip back
        if (showThinking) {
            Text(
                text = "🤔",
                fontSize = 48.sp,
                modifier = Modifier
                    .offset(y = (-70).dp)
                    .scale(scale)
            )
        }

        // X mark when not matching
        if (cardsFlipped && progress > 0.4f && progress < 0.6f) {
            Text(
                text = "✗",
                fontSize = 36.sp,
                color = Color(0xFFF44336),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-70).dp)
            )
        }
    }
}

@Composable
private fun MemoryTapToContinueHint(
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

private data class MemoryTutorialStep(
    val emoji: String,
    val title: String,
    val description: String,
    val animationType: MemoryAnimationType
)

private enum class MemoryAnimationType {
    NONE,
    TAP_CARD,
    MATCH_CARDS,
    REMEMBER
}
