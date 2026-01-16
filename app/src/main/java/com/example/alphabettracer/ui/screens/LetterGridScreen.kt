package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.alphabetList
import com.example.alphabettracer.model.MatchResult
import kotlinx.coroutines.delay

@Composable
fun LetterGridScreen(
    letterResults: Map<Int, MatchResult>,
    onLetterPracticeClicked: () -> Unit,
    onWordSearchClicked: () -> Unit = {},
    onStickBuilderClicked: () -> Unit = {},
    onCountingGameClicked: () -> Unit = {},
    onMemoryMatchClicked: () -> Unit = {},
    onPatternGameClicked: () -> Unit = {},
    onColoringClicked: () -> Unit = {}
) {
    // Animation states for staggered entry
    var showHeader by remember { mutableStateOf(false) }
    var showLetterCard by remember { mutableStateOf(false) }
    var showColoringCard by remember { mutableStateOf(false) }
    var showWordSearch by remember { mutableStateOf(false) }
    var showStickBuilder by remember { mutableStateOf(false) }
    var showMiniGames by remember { mutableStateOf(false) }

    // Trigger staggered animations
    LaunchedEffect(Unit) {
        showHeader = true
        delay(100)
        showLetterCard = true
        delay(150)
        showWordSearch = true
        delay(100)
        showStickBuilder = true
        delay(100)
        showMiniGames = true
        delay(100)
        showColoringCard = true
    }

    // Count letters with at least GOOD result for progress
    val completedCount = letterResults.count { it.value.ordinal >= MatchResult.GOOD.ordinal }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Fun decorative header with animation
        AnimatedVisibility(
            visible = showHeader,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🎮", fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Let's Learn & Play!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
                Spacer(Modifier.width(8.dp))
                Text("🎯", fontSize = 28.sp)
            }
        }

        // Letter Practice Card with animation
        AnimatedVisibility(
            visible = showLetterCard,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400)
            )
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "card_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onLetterPracticeClicked() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("ABC", fontSize = 36.sp)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "Practice Letters",
                                fontSize = 22.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Trace A-Z with your finger",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("abc", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "$completedCount of ${alphabetList.size} letters practiced",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Games Section Header
        AnimatedVisibility(
            visible = showWordSearch,
            enter = fadeIn(tween(200))
        ) {
            Text(
                "Fun Games",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Word Search Button with animation
        AnimatedVisibility(
            visible = showWordSearch,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400)
            )
        ) {
            AnimatedGameButton(
                onClick = onWordSearchClicked,
                containerColor = Color(0xFF2196F3),
                leftEmoji = "🔍",
                text = "Word Search",
                rightEmoji = "🎯",
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Stick Builder Button with animation
        AnimatedVisibility(
            visible = showStickBuilder,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400)
            )
        ) {
            AnimatedGameButton(
                onClick = onStickBuilderClicked,
                containerColor = Color(0xFF8B4513),
                leftEmoji = "🪵",
                text = "Stick Builder",
                rightEmoji = "🔢",
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Mini games row with animation
        AnimatedVisibility(
            visible = showMiniGames,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400)
            )
        ) {
            Column {
                Text(
                    "Quick Games",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Counting Game Button
                    MiniGameButton(
                        onClick = onCountingGameClicked,
                        containerColor = Color(0xFF4CAF50),
                        emoji = "🔢",
                        text = "Count",
                        modifier = Modifier.weight(1f)
                    )

                    // Memory Match Button
                    MiniGameButton(
                        onClick = onMemoryMatchClicked,
                        containerColor = Color(0xFF9C27B0),
                        emoji = "🧠",
                        text = "Memory",
                        modifier = Modifier.weight(1f)
                    )

                    // Pattern Game Button
                    MiniGameButton(
                        onClick = onPatternGameClicked,
                        containerColor = Color(0xFFFF9800),
                        emoji = "🧩",
                        text = "Pattern",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Coloring Book Card with animation (slides from bottom)
        AnimatedVisibility(
            visible = showColoringCard,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(400)
            )
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.96f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "coloring_card_scale"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .scale(scale)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onColoringClicked() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE91E63)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🎨", fontSize = 36.sp)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                "Coloring Book",
                                fontSize = 22.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Color fun shapes & pictures",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Text("🖌️", fontSize = 36.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "12 pictures to color!",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedGameButton(
    onClick: () -> Unit,
    containerColor: Color,
    leftEmoji: String,
    text: String,
    rightEmoji: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource
    ) {
        Text(leftEmoji, fontSize = 24.sp)
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Text(rightEmoji, fontSize = 24.sp)
    }
}

@Composable
private fun MiniGameButton(
    onClick: () -> Unit,
    containerColor: Color,
    emoji: String,
    text: String,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "mini_button_scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .height(70.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(2.dp))
            Text(
                text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
