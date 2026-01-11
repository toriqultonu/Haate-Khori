package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.ChallengeType
import com.example.alphabettracer.data.NumberStickPatterns
import com.example.alphabettracer.data.StickBuilderLevels
import com.example.alphabettracer.data.StickBuilderStorage
import com.example.alphabettracer.data.StickSegment
import com.example.alphabettracer.data.stickColors
import kotlinx.coroutines.launch
import kotlin.math.sqrt

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
    val colorIndex: Int
)

// Represents a stick being dragged (for overlay rendering)
data class DraggingStick(
    val id: Int,
    val colorIndex: Int,
    val currentX: Float,
    val currentY: Float
)

// Calculate distance from point to line segment
private fun distanceToSegment(
    px: Float, py: Float,
    x1: Float, y1: Float,
    x2: Float, y2: Float
): Float {
    val dx = x2 - x1
    val dy = y2 - y1
    val lengthSq = dx * dx + dy * dy

    if (lengthSq == 0f) {
        return sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1))
    }

    val t = (((px - x1) * dx + (py - y1) * dy) / lengthSq).coerceIn(0f, 1f)
    val projX = x1 + t * dx
    val projY = y1 + t * dy

    return sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY))
}

// Find closest unmatched segment within threshold
private fun findMatchingSegment(
    dropX: Float,
    dropY: Float,
    canvasBounds: Rect,
    pattern: List<StickSegment>,
    matchedIds: Set<Int>,
    threshold: Float = 0.18f
): StickSegment? {
    if (canvasBounds.isEmpty) return null

    // Convert screen position to normalized (0-1) canvas coordinates
    val normX = (dropX - canvasBounds.left) / canvasBounds.width
    val normY = (dropY - canvasBounds.top) / canvasBounds.height

    // Must be within canvas bounds (with tolerance)
    if (normX < -0.15f || normX > 1.15f || normY < -0.15f || normY > 1.15f) {
        return null
    }

    var bestSegment: StickSegment? = null
    var bestDistance = Float.MAX_VALUE

    for (segment in pattern) {
        if (matchedIds.contains(segment.id)) continue

        val dist = distanceToSegment(
            normX, normY,
            segment.startX, segment.startY,
            segment.endX, segment.endY
        )

        if (dist < bestDistance && dist < threshold) {
            bestDistance = dist
            bestSegment = segment
        }
    }

    return bestSegment
}

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
    val matchedSegmentIds = remember { mutableStateListOf<Int>() }
    var showHint by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }

    // Canvas bounds for hit detection
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }

    // Dragging state for overlay - renders the dragged stick above everything
    var draggingStick by remember { mutableStateOf<DraggingStick?>(null) }

    // Tray sticks
    val traySticks = remember { mutableStateListOf<TrayStick>() }
    if (traySticks.isEmpty()) {
        repeat(6) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    // Reset function
    fun resetLevel() {
        placedSticks.clear()
        matchedSegmentIds.clear()
        showHint = false
        levelCompleted = false
        showCelebration = false
        draggingStick = null
        traySticks.clear()
        repeat(6) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    // Check completion
    fun checkCompletion(): Boolean {
        return matchedSegmentIds.size >= targetPattern.size
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

    // Main content wrapped in Box for overlay support
    Box(modifier = Modifier.fillMaxSize()) {
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
                    // Target number outline (dashed) - ONLY shown when hint is active
                    if (showHint) {
                        NumberOutlineCanvas(
                            pattern = targetPattern,
                            matchedIds = matchedSegmentIds.toSet(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                                .onGloballyPositioned { coords ->
                                    canvasBounds = coords.boundsInRoot()
                                }
                        )
                    } else {
                        // Still need to track bounds even when not showing hint
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp)
                                .onGloballyPositioned { coords ->
                                    canvasBounds = coords.boundsInRoot()
                                }
                        )
                    }

                    // Placed sticks - always visible
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
                canvasBounds = canvasBounds,
                matchedIds = matchedSegmentIds.toSet(),
                onDragStart = { stick, x, y ->
                    draggingStick = DraggingStick(stick.id, stick.colorIndex, x, y)
                },
                onDragMove = { x, y ->
                    draggingStick = draggingStick?.copy(currentX = x, currentY = y)
                },
                onDragEnd = { stick, dropX, dropY ->
                    // Find matching segment
                    val matched = findMatchingSegment(
                        dropX = dropX,
                        dropY = dropY,
                        canvasBounds = canvasBounds,
                        pattern = targetPattern,
                        matchedIds = matchedSegmentIds.toSet()
                    )

                    if (matched != null) {
                        // Place the stick
                        placedSticks.add(
                            PlacedStick(
                                id = System.currentTimeMillis().toInt(),
                                colorIndex = stick.colorIndex,
                                startX = matched.startX,
                                startY = matched.startY,
                                endX = matched.endX,
                                endY = matched.endY,
                                matchedSegmentId = matched.id
                            )
                        )
                        matchedSegmentIds.add(matched.id)

                        // Remove the stick from tray
                        traySticks.removeIf { it.id == stick.id }
                    }

                    draggingStick = null
                },
                onDragCancel = {
                    draggingStick = null
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

        // Dragging stick overlay - renders ABOVE everything
        draggingStick?.let { dragStick ->
            DraggingStickOverlay(
                colorIndex = dragStick.colorIndex,
                x = dragStick.currentX,
                y = dragStick.currentY
            )
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
}

@Composable
private fun DraggingStickOverlay(
    colorIndex: Int,
    x: Float,
    y: Float
) {
    val color = stickColors[colorIndex % stickColors.size]

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    translationX = x - 8.dp.toPx() // Center the stick
                    translationY = y - 35.dp.toPx() // Center the stick
                    scaleX = 1.4f
                    scaleY = 1.4f
                    shadowElevation = 16f
                }
                .width(16.dp)
                .height(70.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(8.dp)
                )
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.95f),
                            color,
                            color.copy(alpha = 0.85f)
                        )
                    )
                )
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
    matchedIds: Set<Int>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)

        pattern.forEach { segment ->
            val isMatched = matchedIds.contains(segment.id)
            if (isMatched) return@forEach // Don't draw matched segments

            val startX = segment.startX * size.width
            val startY = segment.startY * size.height
            val endX = segment.endX * size.width
            val endY = segment.endY * size.height

            // Dashed outline with hint color
            val hintColor = stickColors[segment.id % stickColors.size]
            drawLine(
                color = hintColor.copy(alpha = 0.4f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = 24f,
                cap = StrokeCap.Round,
                pathEffect = dashEffect
            )
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
                strokeWidth = 22f,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun StickTray(
    traySticks: List<TrayStick>,
    targetPattern: List<StickSegment>,
    canvasBounds: Rect,
    matchedIds: Set<Int>,
    onDragStart: (TrayStick, Float, Float) -> Unit,
    onDragMove: (Float, Float) -> Unit,
    onDragEnd: (TrayStick, Float, Float) -> Unit,
    onDragCancel: () -> Unit
) {
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
            Card(
                modifier = Modifier.fillMaxSize(),
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
                    traySticks.forEach { stick ->
                        DraggableStick(
                            stick = stick,
                            onDragStart = onDragStart,
                            onDragMove = onDragMove,
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragCancel
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
    onDragStart: (TrayStick, Float, Float) -> Unit,
    onDragMove: (Float, Float) -> Unit,
    onDragEnd: (TrayStick, Float, Float) -> Unit,
    onDragCancel: () -> Unit
) {
    val color = stickColors[stick.colorIndex % stickColors.size]
    val scope = rememberCoroutineScope()

    // Animated offset for smooth spring-back
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }

    // Track stick's base position in screen coordinates
    var stickBasePosition by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 0.8f else 1f, // Shrink when dragging (overlay shows the big one)
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "stickScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isDragging) 0.3f else 1f, // Fade when dragging
        animationSpec = spring(),
        label = "stickAlpha"
    )

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                val bounds = coords.boundsInRoot()
                stickBasePosition = bounds.center
            }
            .graphicsLayer {
                translationX = offsetX.value
                translationY = offsetY.value
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .width(16.dp)
            .height(70.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.95f),
                        color,
                        color.copy(alpha = 0.85f)
                    )
                )
            )
            .pointerInput(stick.id) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        isDragging = true
                        val startX = stickBasePosition.x
                        val startY = stickBasePosition.y
                        onDragStart(stick, startX, startY)
                    },
                    onDragEnd = {
                        isDragging = false

                        // Calculate where the stick center is now
                        val dropX = stickBasePosition.x + offsetX.value
                        val dropY = stickBasePosition.y + offsetY.value

                        onDragEnd(stick, dropX, dropY)

                        // Spring back to original position
                        scope.launch {
                            launch {
                                offsetX.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                            launch {
                                offsetY.animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        onDragCancel()
                        scope.launch {
                            launch { offsetX.animateTo(0f, spring()) }
                            launch { offsetY.animateTo(0f, spring()) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Immediate snap to finger position
                        scope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                        // Update overlay position
                        val currentX = stickBasePosition.x + offsetX.value + dragAmount.x
                        val currentY = stickBasePosition.y + offsetY.value + dragAmount.y
                        onDragMove(currentX, currentY)
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
