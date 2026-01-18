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
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
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
fun ColoringTutorial(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onSkip: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val tutorialSteps = listOf(
        ColoringTutorialStep(
            emoji = "🎨",
            title = "Coloring Book!",
            description = "Express your creativity with colors",
            animationType = ColoringAnimationType.NONE
        ),
        ColoringTutorialStep(
            emoji = "🖼️",
            title = "Pick a Picture",
            description = "Choose any shape you want to color",
            animationType = ColoringAnimationType.SELECT_SHAPE
        ),
        ColoringTutorialStep(
            emoji = "👆",
            title = "Draw & Color",
            description = "Touch and drag to fill in the picture",
            animationType = ColoringAnimationType.DRAW
        ),
        ColoringTutorialStep(
            emoji = "🌈",
            title = "Choose Colors",
            description = "Tap any color from the palette below",
            animationType = ColoringAnimationType.PICK_COLOR
        ),
        ColoringTutorialStep(
            emoji = "🧹",
            title = "Eraser & Tools",
            description = "Use the eraser to fix mistakes, adjust brush size",
            animationType = ColoringAnimationType.USE_ERASER
        ),
        ColoringTutorialStep(
            emoji = "🌟",
            title = "You're Ready!",
            description = "Create your masterpiece! Have fun!",
            animationType = ColoringAnimationType.NONE
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
                ColoringInstructionCard(
                    step = tutorialSteps[currentStep],
                    currentStepIndex = currentStep,
                    totalSteps = tutorialSteps.size,
                    onSkip = onSkip
                )
            }

            // Animated hand pointer
            if (tutorialSteps[currentStep].animationType != ColoringAnimationType.NONE) {
                ColoringAnimatedHand(
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
                ColoringTapToContinueHint(
                    isLastStep = currentStep == tutorialSteps.size - 1,
                    onGotIt = onDismiss
                )
            }
        }
    }
}

@Composable
private fun ColoringInstructionCard(
    step: ColoringTutorialStep,
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
                                        Color(0xFFE91E63)
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
                color = Color(0xFFE91E63),
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
private fun ColoringAnimatedHand(
    animationType: ColoringAnimationType,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coloringHand")

    // Animation progress (0 to 1)
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Restart
        ),
        label = "coloringProgress"
    )

    // Pulsing effect for the hand
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coloringScale"
    )

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        when (animationType) {
            ColoringAnimationType.SELECT_SHAPE -> {
                SelectShapeAnimation(progress = progress, scale = scale)
            }
            ColoringAnimationType.DRAW -> {
                DrawAnimation(progress = progress, scale = scale)
            }
            ColoringAnimationType.PICK_COLOR -> {
                PickColorAnimation(progress = progress, scale = scale)
            }
            ColoringAnimationType.USE_ERASER -> {
                UseEraserAnimation(progress = progress, scale = scale)
            }
            ColoringAnimationType.NONE -> {
                // No animation
            }
        }
    }
}

@Composable
private fun SelectShapeAnimation(
    progress: Float,
    scale: Float
) {
    val shapes = listOf("⭐", "❤️", "🌸")
    val selectedIndex = ((progress * 3).toInt() % 3)

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            shapes.forEachIndexed { index, shape ->
                Card(
                    modifier = Modifier
                        .size(70.dp)
                        .scale(if (index == selectedIndex) 1.1f else 1f),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index == selectedIndex)
                            Color(0xFFE91E63).copy(alpha = 0.2f)
                        else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = if (index == selectedIndex) 8.dp else 4.dp
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(shape, fontSize = 36.sp)
                    }
                }
            }
        }

        // Hand pointing at selected shape
        val handOffsetX = when (selectedIndex) {
            0 -> (-70).dp
            1 -> 0.dp
            else -> 70.dp
        }

        Text(
            text = "👆",
            fontSize = 40.sp,
            modifier = Modifier
                .offset(x = handOffsetX, y = 55.dp)
                .scale(scale)
        )
    }
}

@Composable
private fun DrawAnimation(
    progress: Float,
    scale: Float
) {
    val strokeColor = Color(0xFFE91E63)

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Canvas with drawing
        Canvas(modifier = Modifier.size(150.dp)) {
            // Draw a star outline
            val centerX = size.width / 2
            val centerY = size.height / 2
            val outerRadius = size.width * 0.4f

            // Simple shape outline
            drawCircle(
                color = Color.Gray.copy(alpha = 0.3f),
                radius = outerRadius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3.dp.toPx())
            )

            // Colored stroke following progress
            if (progress > 0.1f) {
                val path = Path()
                val sweepProgress = progress.coerceIn(0f, 0.9f)

                // Draw a curved coloring path
                val startAngle = -90f
                val sweepAngle = 360f * sweepProgress

                path.addArc(
                    oval = androidx.compose.ui.geometry.Rect(
                        centerX - outerRadius,
                        centerY - outerRadius,
                        centerX + outerRadius,
                        centerY + outerRadius
                    ),
                    startAngleDegrees = startAngle,
                    sweepAngleDegrees = sweepAngle
                )

                drawPath(
                    path = path,
                    color = strokeColor.copy(alpha = 0.7f),
                    style = Stroke(
                        width = 20.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // Hand following the drawing
        val angle = progress * 360f - 90f
        val radius = 55.dp
        val handOffsetX = (radius.value * kotlin.math.cos(Math.toRadians(angle.toDouble()))).dp
        val handOffsetY = (radius.value * kotlin.math.sin(Math.toRadians(angle.toDouble()))).dp

        Text(
            text = "👆",
            fontSize = 40.sp,
            modifier = Modifier
                .offset(x = handOffsetX, y = handOffsetY)
                .scale(if (progress > 0.1f) scale else 1f)
        )
    }
}

@Composable
private fun PickColorAnimation(
    progress: Float,
    scale: Float
) {
    val colors = listOf(
        Color(0xFFF44336),
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFFEB3B),
        Color(0xFFE91E63)
    )
    val selectedIndex = ((progress * 5).toInt() % 5)

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Color palette
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            colors.forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .size(if (index == selectedIndex) 50.dp else 40.dp)
                        .background(color, CircleShape)
                        .border(
                            width = if (index == selectedIndex) 3.dp else 1.dp,
                            color = if (index == selectedIndex) Color.Black else Color.Gray.copy(alpha = 0.3f),
                            shape = CircleShape
                        )
                )
            }
        }

        // Hand tapping colors
        val handOffsetX = (-80 + selectedIndex * 48).dp

        Text(
            text = "👆",
            fontSize = 40.sp,
            modifier = Modifier
                .offset(x = handOffsetX, y = 45.dp)
                .scale(scale)
        )

        // Show selected color indicator
        Box(
            modifier = Modifier
                .offset(y = (-60).dp)
                .size(40.dp)
                .background(colors[selectedIndex], CircleShape)
                .border(2.dp, Color.White, CircleShape)
        )
    }
}

@Composable
private fun UseEraserAnimation(
    progress: Float,
    scale: Float
) {
    val isEraserSelected = progress > 0.4f

    Box(
        modifier = Modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Eraser button
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (isEraserSelected) Color(0xFFE0E0E0) else Color.White,
                        CircleShape
                    )
                    .border(
                        width = if (isEraserSelected) 3.dp else 1.dp,
                        color = if (isEraserSelected) Color.Black else Color.Gray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🧹", fontSize = 32.sp)
            }

            // Brush size indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🖌️", fontSize = 24.sp)
                Spacer(Modifier.height(4.dp))
                // Animated brush size
                val brushSize = (20 + (progress * 30)).dp.coerceIn(20.dp, 45.dp)
                Box(
                    modifier = Modifier
                        .size(brushSize)
                        .background(Color(0xFFE91E63), CircleShape)
                )
            }
        }

        // Hand pointing
        val handOffsetX = if (isEraserSelected) (-40).dp else 50.dp

        Text(
            text = "👆",
            fontSize = 40.sp,
            modifier = Modifier
                .offset(x = handOffsetX, y = 50.dp)
                .scale(scale)
        )
    }
}

@Composable
private fun ColoringTapToContinueHint(
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
                text = "Let's Color!",
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

private data class ColoringTutorialStep(
    val emoji: String,
    val title: String,
    val description: String,
    val animationType: ColoringAnimationType
)

private enum class ColoringAnimationType {
    NONE,
    SELECT_SHAPE,
    DRAW,
    PICK_COLOR,
    USE_ERASER
}
