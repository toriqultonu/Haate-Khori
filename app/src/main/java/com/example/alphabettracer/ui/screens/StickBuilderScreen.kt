package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.ChallengeType
import com.example.alphabettracer.data.NumberStickPatterns
import com.example.alphabettracer.data.StickBuilderLevels
import com.example.alphabettracer.data.StickBuilderStorage
import com.example.alphabettracer.data.StickSegment
import com.example.alphabettracer.data.stickColors
import kotlin.math.roundToInt

// Represents a stick placed on the canvas
data class PlacedStick(
    val id: Int,
    val colorIndex: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val matchedSegmentId: Int = -1
)

// Represents a draggable stick in the tray
data class TrayStick(
    val id: Int,
    val colorIndex: Int,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var isDragging: Boolean = false
)

@Composable
fun StickBuilderScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var currentLevelId by remember { mutableIntStateOf(StickBuilderStorage.getCurrentLevel(context)) }
    val challenge = remember(currentLevelId) {
        StickBuilderLevels.getChallengeById(currentLevelId) ?: StickBuilderLevels.allChallenges.first()
    }
    val targetNumber = remember(challenge) { StickBuilderLevels.getTargetNumber(challenge) }
    val targetPattern = remember(targetNumber) { NumberStickPatterns.getPatternForNumber(targetNumber) }

    // Game state
    val placedSticks = remember { mutableStateListOf<PlacedStick>() }
    var showHint by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Tray sticks - replenish when needed
    val traySticks = remember { mutableStateListOf<TrayStick>() }
    if (traySticks.isEmpty()) {
        repeat(6) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    // Reset function
    fun resetLevel() {
        placedSticks.clear()
        showHint = false
        levelCompleted = false
        showCelebration = false
        traySticks.clear()
        repeat(6) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    // Check completion
    fun checkCompletion(): Boolean {
        if (placedSticks.size < targetPattern.size) return false

        val matchedSegments = mutableSetOf<Int>()
        for (placed in placedSticks) {
            if (placed.matchedSegmentId >= 0) {
                matchedSegments.add(placed.matchedSegmentId)
            }
        }
        return matchedSegments.size >= targetPattern.size
    }

    // Navigate to next level
    fun goToNextLevel() {
        if (currentLevelId < StickBuilderLevels.allChallenges.size) {
            currentLevelId++
            StickBuilderStorage.setCurrentLevel(context, currentLevelId)
            resetLevel()
        }
    }

    // Navigate to previous level
    fun goToPreviousLevel() {
        if (currentLevelId > 1) {
            currentLevelId--
            StickBuilderStorage.setCurrentLevel(context, currentLevelId)
            resetLevel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8E1),
                        Color(0xFFE3F2FD),
                        Color(0xFFF3E5F5)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with title and sun mascot
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Title
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪵", fontSize = 24.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Stick Builder",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B4513)
                    )
                }
                Text(
                    "Level ${challenge.id}/${StickBuilderLevels.allChallenges.size}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Sun mascot
            SunMascot()
        }

        Spacer(Modifier.height(12.dp))

        // Challenge display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = challenge.displayText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    textAlign = TextAlign.Center
                )

                if (challenge.type == ChallengeType.EQUATION_RESULT) {
                    Text(
                        text = "Build the answer!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Main game area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE6)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Target number outline (dashed)
                NumberOutlineCanvas(
                    pattern = targetPattern,
                    showHint = showHint,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )

                // Placed sticks
                PlacedSticksCanvas(
                    sticks = placedSticks,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )

                // Hint button (top-left)
                IconButton(
                    onClick = { showHint = !showHint },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            color = if (showHint) Color(0xFF4CAF50) else Color(0xFFFFB74D),
                            shape = CircleShape
                        )
                        .size(48.dp)
                ) {
                    Text(
                        "💡",
                        fontSize = 20.sp
                    )
                }

                // Reset button (bottom-left)
                IconButton(
                    onClick = { resetLevel() },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = CircleShape
                        )
                        .size(48.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color.White
                    )
                }

                // Done button (bottom-right)
                Button(
                    onClick = {
                        if (checkCompletion()) {
                            levelCompleted = true
                            showCelebration = true
                            StickBuilderStorage.saveChallengeCompleted(context, challenge.id)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("DONE!", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stick tray
        StickTray(
            traySticks = traySticks,
            targetPattern = targetPattern,
            placedSticks = placedSticks,
            onStickPlaced = { placedStick ->
                placedSticks.add(placedStick)
            }
        )

        Spacer(Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { goToPreviousLevel() },
                enabled = currentLevelId > 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                Spacer(Modifier.width(4.dp))
                Text("Previous")
            }

            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back Home")
            }

            Button(
                onClick = { goToNextLevel() },
                enabled = currentLevelId < StickBuilderLevels.allChallenges.size,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    // Celebration overlay
    AnimatedVisibility(
        visible = showCelebration,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        CelebrationOverlay(
            onDismiss = { showCelebration = false },
            onNextLevel = {
                showCelebration = false
                goToNextLevel()
            },
            isLastLevel = currentLevelId >= StickBuilderLevels.allChallenges.size
        )
    }
}

@Composable
private fun SunMascot() {
    var rotation by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = spring(),
        label = "sunRotation"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .rotate(animatedRotation),
        contentAlignment = Alignment.Center
    ) {
        Text("🌞", fontSize = 48.sp)
    }
}

@Composable
private fun NumberOutlineCanvas(
    pattern: List<StickSegment>,
    showHint: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

        pattern.forEach { segment ->
            val startX = segment.startX * size.width
            val startY = segment.startY * size.height
            val endX = segment.endX * size.width
            val endY = segment.endY * size.height

            // Dashed outline
            drawLine(
                color = Color.Gray.copy(alpha = 0.5f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 24f,
                cap = StrokeCap.Round,
                pathEffect = dashEffect
            )

            // Hint colored overlay
            if (showHint) {
                val hintColor = stickColors[segment.id % stickColors.size]
                drawLine(
                    color = hintColor.copy(alpha = 0.3f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 20f,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
private fun PlacedSticksCanvas(
    sticks: List<PlacedStick>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        sticks.forEach { stick ->
            val color = stickColors[stick.colorIndex % stickColors.size]
            drawLine(
                color = color,
                start = Offset(stick.startX * size.width, stick.startY * size.height),
                end = Offset(stick.endX * size.width, stick.endY * size.height),
                strokeWidth = 20f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun StickTray(
    traySticks: List<TrayStick>,
    targetPattern: List<StickSegment>,
    placedSticks: MutableList<PlacedStick>,
    onStickPlaced: (PlacedStick) -> Unit
) {
    val density = LocalDensity.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7CCC8)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp)
        ) {
            // Inner tray with sticks
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFBCAAA4)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    traySticks.forEachIndexed { index, stick ->
                        DraggableStick(
                            stick = stick,
                            targetPattern = targetPattern,
                            onStickPlaced = { placedStick ->
                                onStickPlaced(placedStick)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableStick(
    stick: TrayStick,
    targetPattern: List<StickSegment>,
    onStickPlaced: (PlacedStick) -> Unit
) {
    val color = stickColors[stick.colorIndex % stickColors.size]
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.2f else 1f,
        animationSpec = spring(),
        label = "stickScale"
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .scale(scale)
            .width(12.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .pointerInput(stick.id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        isDragging = false
                        // Check if dropped in valid position
                        // For simplicity, we'll place at a random unmatched segment
                        val unmatchedSegments = targetPattern.filter { segment ->
                            true // Could add logic to check if segment is already matched
                        }
                        if (unmatchedSegments.isNotEmpty()) {
                            val targetSegment = unmatchedSegments.random()
                            onStickPlaced(
                                PlacedStick(
                                    id = System.currentTimeMillis().toInt(),
                                    colorIndex = stick.colorIndex,
                                    startX = targetSegment.startX,
                                    startY = targetSegment.startY,
                                    endX = targetSegment.endX,
                                    endY = targetSegment.endY,
                                    matchedSegmentId = targetSegment.id
                                )
                            )
                        }
                        // Reset position
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDragCancel = {
                        isDragging = false
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    )
}

@Composable
private fun CelebrationOverlay(
    onDismiss: () -> Unit,
    onNextLevel: () -> Unit,
    isLastLevel: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎉", fontSize = 64.sp)

                Spacer(Modifier.height(16.dp))

                Text(
                    "Awesome!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                Text(
                    "You built the number perfectly!",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9800)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Stay Here")
                    }

                    if (!isLastLevel) {
                        Button(
                            onClick = onNextLevel,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next Level")
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next"
                            )
                        }
                    }
                }
            }
        }
    }
}
