package com.example.alphabettracer.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.model.MatchResult

@Composable
fun LetterGridItem(
    letter: Char,
    result: MatchResult,
    onClick: () -> Unit
) {
    // Colors based on result: Green (Excellent), Yellow (Good), Red (Poor), White (None)
    val (backgroundColor, textColor, icon) = when (result) {
        MatchResult.EXCELLENT -> Triple(Color(0xFF4CAF50), Color.White, "⭐")  // Green
        MatchResult.GOOD -> Triple(Color(0xFFFFC107), Color(0xFF333333), "👍")  // Yellow
        MatchResult.POOR -> Triple(Color(0xFFFF5722), Color.White, "🔄")  // Red/Orange
        MatchResult.NONE -> Triple(Color.White, Color(0xFF333333), "")  // White (not attempted)
    }

    // Interaction source for press animation
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bounce animation on press
    val scale = remember { Animatable(1f) }
    LaunchedEffect(isPressed) {
        if (isPressed) {
            scale.animateTo(
                targetValue = 0.9f,
                animationSpec = tween(100, easing = FastOutSlowInEasing)
            )
        } else {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    // Subtle pulse animation for unattempted letters
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (result == MatchResult.NONE) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Sparkle rotation for excellent items
    val sparkleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (result == MatchResult.EXCELLENT) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotation"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale.value * pulseScale)
            .shadow(
                elevation = if (result == MatchResult.EXCELLENT) 8.dp else 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = letter.toString(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (icon.isNotEmpty()) {
                    Text(
                        text = icon,
                        fontSize = 14.sp,
                        modifier = if (result == MatchResult.EXCELLENT) {
                            Modifier.graphicsLayer { rotationZ = sparkleRotation }
                        } else {
                            Modifier
                        }
                    )
                }
            }
        }
    }
}