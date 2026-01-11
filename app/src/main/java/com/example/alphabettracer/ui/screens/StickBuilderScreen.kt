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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.ChallengeType
import com.example.alphabettracer.data.DigitSegments
import com.example.alphabettracer.data.SegmentPosition
import com.example.alphabettracer.data.SevenSegmentLayout
import com.example.alphabettracer.data.StickBuilderLevels
import com.example.alphabettracer.data.StickBuilderStorage
import com.example.alphabettracer.data.stickColors
import kotlin.math.roundToInt
import kotlin.math.sqrt

// A stick that has been placed on the canvas
data class PlacedStick(
    val id: Int,
    val colorIndex: Int,
    val x: Float,           // Screen position X
    val y: Float,           // Screen position Y
    val isHorizontal: Boolean,
    val rotation: Float = if (isHorizontal) 0f else 90f  // 0 = horizontal, 90 = vertical
)

// A stick in the tray that can be dragged
data class TrayStick(
    val id: Int,
    val colorIndex: Int
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
    val requiredStickCount = remember(targetNumber) { DigitSegments.getStickCountForDigit(targetNumber) }

    // Placed sticks on canvas
    val placedSticks = remember { mutableStateListOf<PlacedStick>() }

    // Tray sticks (all same alignment - vertical in tray)
    val traySticks = remember { mutableStateListOf<TrayStick>() }

    // Initialize tray with enough sticks
    if (traySticks.isEmpty()) {
        repeat(requiredStickCount.coerceAtLeast(7)) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    var showHint by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var recognizedNumber by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Canvas bounds for coordinate conversion
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }

    // Currently dragging
    var draggingStickId by remember { mutableStateOf<Int?>(null) }
    var draggingPosition by remember { mutableStateOf(Offset.Zero) }
    var draggingIsHorizontal by remember { mutableStateOf(true) }  // Current rotation while dragging
    var draggingFromTray by remember { mutableStateOf(false) }

    // Reset function
    fun resetLevel() {
        placedSticks.clear()
        traySticks.clear()
        showHint = false
        levelCompleted = false
        showCelebration = false
        recognizedNumber = null
        showResult = false
        isCorrect = false
        draggingStickId = null

        // Recreate tray sticks
        repeat(requiredStickCount.coerceAtLeast(7)) { index ->
            traySticks.add(TrayStick(id = index, colorIndex = index % stickColors.size))
        }
    }

    // Find which segment a stick is closest to
    fun findMatchingSegment(
        stickX: Float,
        stickY: Float,
        isHorizontal: Boolean,
        canvasWidth: Float,
        canvasHeight: Float
    ): SegmentPosition? {
        if (canvasBounds.isEmpty) return null

        // Normalize position to 0-1 range within canvas
        val normX = (stickX - canvasBounds.left) / canvasBounds.width
        val normY = (stickY - canvasBounds.top) / canvasBounds.height

        // Check each segment slot
        val threshold = 0.15f

        for (slot in SevenSegmentLayout.slots) {
            val slotIsHorizontal = slot.isHorizontal
            if (isHorizontal != slotIsHorizontal) continue

            val dx = normX - slot.centerX
            val dy = normY - slot.centerY
            val dist = sqrt(dx * dx + dy * dy)

            if (dist < threshold) {
                return slot.position
            }
        }
        return null
    }

    // Recognize number from placed sticks
    fun recognizeNumberFromSticks(): Int? {
        if (canvasBounds.isEmpty) return null

        val activeSegments = mutableSetOf<SegmentPosition>()

        for (stick in placedSticks) {
            val segment = findMatchingSegment(
                stick.x, stick.y,
                stick.isHorizontal,
                canvasBounds.width,
                canvasBounds.height
            )
            if (segment != null) {
                activeSegments.add(segment)
            }
        }

        return DigitSegments.recognizeDigit(activeSegments)
    }

    // Check answer
    fun checkAnswer() {
        val number = recognizeNumberFromSticks()
        recognizedNumber = number
        isCorrect = number == targetNumber
        showResult = true

        if (isCorrect) {
            levelCompleted = true
            showCelebration = true
            StickBuilderStorage.saveChallengeCompleted(context, challenge.id)
        }
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Drag sticks • Double-tap to rotate",
                        fontSize = 12.sp,
                        color = Color(0xFF8B4513)
                    )
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
                    modifier = Modifier
                        .fillMaxSize()
                        .onGloballyPositioned { coords ->
                            canvasBounds = coords.boundsInRoot()
                        }
                ) {
                    // Draw 7-segment outline guide
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw segment guides
                        val guideColor = if (showHint) Color(0xFF4CAF50).copy(alpha = 0.5f)
                                        else Color.LightGray.copy(alpha = 0.3f)
                        val strokeWidth = if (showHint) 24f else 18f

                        // Calculate positions for 7-segment display
                        val left = canvasWidth * 0.2f
                        val right = canvasWidth * 0.8f
                        val top = canvasHeight * 0.1f
                        val middle = canvasHeight * 0.5f
                        val bottom = canvasHeight * 0.9f

                        // A - Top horizontal
                        drawLine(guideColor, Offset(left, top), Offset(right, top), strokeWidth, StrokeCap.Round)
                        // B - Top-right vertical
                        drawLine(guideColor, Offset(right, top), Offset(right, middle), strokeWidth, StrokeCap.Round)
                        // C - Bottom-right vertical
                        drawLine(guideColor, Offset(right, middle), Offset(right, bottom), strokeWidth, StrokeCap.Round)
                        // D - Bottom horizontal
                        drawLine(guideColor, Offset(left, bottom), Offset(right, bottom), strokeWidth, StrokeCap.Round)
                        // E - Bottom-left vertical
                        drawLine(guideColor, Offset(left, middle), Offset(left, bottom), strokeWidth, StrokeCap.Round)
                        // F - Top-left vertical
                        drawLine(guideColor, Offset(left, top), Offset(left, middle), strokeWidth, StrokeCap.Round)
                        // G - Middle horizontal
                        drawLine(guideColor, Offset(left, middle), Offset(right, middle), strokeWidth, StrokeCap.Round)

                        // If hint is on, highlight the segments needed for target number
                        if (showHint) {
                            val targetSegments = DigitSegments.getSegmentsForDigit(targetNumber)
                            val hintColor = Color(0xFF4CAF50).copy(alpha = 0.7f)

                            if (SegmentPosition.A in targetSegments) {
                                drawLine(hintColor, Offset(left, top), Offset(right, top), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.B in targetSegments) {
                                drawLine(hintColor, Offset(right, top), Offset(right, middle), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.C in targetSegments) {
                                drawLine(hintColor, Offset(right, middle), Offset(right, bottom), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.D in targetSegments) {
                                drawLine(hintColor, Offset(left, bottom), Offset(right, bottom), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.E in targetSegments) {
                                drawLine(hintColor, Offset(left, middle), Offset(left, bottom), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.F in targetSegments) {
                                drawLine(hintColor, Offset(left, top), Offset(left, middle), strokeWidth + 4, StrokeCap.Round)
                            }
                            if (SegmentPosition.G in targetSegments) {
                                drawLine(hintColor, Offset(left, middle), Offset(right, middle), strokeWidth + 4, StrokeCap.Round)
                            }
                        }
                    }

                    // Render placed sticks
                    placedSticks.forEach { stick ->
                        PlacedStickView(
                            stick = stick,
                            canvasBounds = canvasBounds,
                            onDragStart = {
                                draggingStickId = stick.id
                                draggingPosition = Offset(stick.x, stick.y)
                                draggingIsHorizontal = stick.isHorizontal
                                draggingFromTray = false
                            },
                            onDrag = { offset ->
                                draggingPosition = Offset(
                                    draggingPosition.x + offset.x,
                                    draggingPosition.y + offset.y
                                )
                            },
                            onDragEnd = {
                                // Update stick position
                                val index = placedSticks.indexOfFirst { it.id == stick.id }
                                if (index >= 0) {
                                    placedSticks[index] = stick.copy(
                                        x = draggingPosition.x,
                                        y = draggingPosition.y,
                                        isHorizontal = draggingIsHorizontal
                                    )
                                }
                                draggingStickId = null
                            },
                            onDoubleTap = {
                                // Rotate the stick
                                val index = placedSticks.indexOfFirst { it.id == stick.id }
                                if (index >= 0) {
                                    val currentStick = placedSticks[index]
                                    placedSticks[index] = currentStick.copy(
                                        isHorizontal = !currentStick.isHorizontal
                                    )
                                }
                            },
                            isDragging = draggingStickId == stick.id
                        )
                    }

                    // Hint button
                    IconButton(
                        onClick = { showHint = !showHint },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .background(
                                color = if (showHint) Color(0xFF4CAF50) else Color(0xFFFFB74D),
                                shape = CircleShape
                            )
                            .size(44.dp)
                    ) {
                        Text("💡", fontSize = 18.sp)
                    }

                    // Reset button
                    IconButton(
                        onClick = { resetLevel() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = Color(0xFFFF5722),
                                shape = CircleShape
                            )
                            .size(44.dp)
                    ) {
                        Icon(Icons.Default.Refresh, "Reset", tint = Color.White)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stick tray
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD7CCC8)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        "Drag sticks from here:",
                        fontSize = 12.sp,
                        color = Color(0xFF5D4037),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFBCAAA4)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            traySticks.forEach { stick ->
                                DraggableTrayStick(
                                    stick = stick,
                                    onDragStart = { startPos ->
                                        draggingStickId = stick.id + 1000  // Offset ID for tray sticks
                                        draggingPosition = startPos
                                        draggingIsHorizontal = false  // Start vertical
                                        draggingFromTray = true
                                    },
                                    onDrag = { offset ->
                                        draggingPosition = Offset(
                                            draggingPosition.x + offset.x,
                                            draggingPosition.y + offset.y
                                        )
                                    },
                                    onDragEnd = { dropPos ->
                                        // Check if dropped on canvas area
                                        if (dropPos.x >= canvasBounds.left &&
                                            dropPos.x <= canvasBounds.right &&
                                            dropPos.y >= canvasBounds.top &&
                                            dropPos.y <= canvasBounds.bottom) {

                                            // Add as placed stick
                                            placedSticks.add(
                                                PlacedStick(
                                                    id = System.currentTimeMillis().toInt(),
                                                    colorIndex = stick.colorIndex,
                                                    x = dropPos.x,
                                                    y = dropPos.y,
                                                    isHorizontal = draggingIsHorizontal
                                                )
                                            )
                                            // Remove from tray
                                            traySticks.removeIf { it.id == stick.id }
                                        }
                                        draggingStickId = null
                                        draggingFromTray = false
                                    },
                                    isDragging = draggingStickId == stick.id + 1000
                                )
                            }

                            // Show empty slots if not enough sticks
                            if (traySticks.isEmpty()) {
                                Text(
                                    "All sticks placed!",
                                    fontSize = 14.sp,
                                    color = Color(0xFF5D4037)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Check Answer button
            Button(
                onClick = { checkAnswer() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("CHECK ANSWER", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Result feedback
            AnimatedVisibility(visible = showResult) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isCorrect) "Correct!" else "Try Again!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                        if (recognizedNumber != null) {
                            Text("You made: $recognizedNumber", fontSize = 16.sp, color = Color.Gray)
                        } else {
                            Text("Couldn't recognize a number", fontSize = 16.sp, color = Color.Gray)
                        }
                        Text("Expected: $targetNumber", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { goToPreviousLevel() },
                    enabled = currentLevelId > 1,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Previous")
                    Spacer(Modifier.width(4.dp))
                    Text("Previous")
                }

                Button(
                    onClick = onBackPressed,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back Home")
                }

                Button(
                    onClick = { goToNextLevel() },
                    enabled = currentLevelId < StickBuilderLevels.allChallenges.size,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        // Dragging overlay - shows the stick being dragged
        if (draggingStickId != null) {
            val colorIndex = if (draggingFromTray) {
                traySticks.find { it.id + 1000 == draggingStickId }?.colorIndex ?: 0
            } else {
                placedSticks.find { it.id == draggingStickId }?.colorIndex ?: 0
            }

            DraggingStickOverlay(
                colorIndex = colorIndex,
                position = draggingPosition,
                isHorizontal = draggingIsHorizontal,
                onRotate = { draggingIsHorizontal = !draggingIsHorizontal }
            )
        }

        // Celebration overlay
        AnimatedVisibility(
            visible = showCelebration,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            CelebrationOverlay(
                targetNumber = targetNumber,
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
private fun DraggableTrayStick(
    stick: TrayStick,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit,
    isDragging: Boolean
) {
    val color = stickColors[stick.colorIndex % stickColors.size]

    var stickPosition by remember { mutableStateOf(Offset.Zero) }
    var accumulatedOffset by remember { mutableStateOf(Offset.Zero) }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 0.5f else 1f,
        animationSpec = spring(),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (isDragging) 0.3f else 1f,
        animationSpec = spring(),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                stickPosition = coords.boundsInRoot().center
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .width(20.dp)
            .height(70.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.9f),
                        color,
                        color.copy(alpha = 0.8f)
                    )
                )
            )
            .pointerInput(stick.id) {
                detectDragGestures(
                    onDragStart = {
                        accumulatedOffset = Offset.Zero
                        onDragStart(stickPosition)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedOffset = Offset(
                            accumulatedOffset.x + dragAmount.x,
                            accumulatedOffset.y + dragAmount.y
                        )
                        onDrag(dragAmount)
                    },
                    onDragEnd = {
                        val finalPos = Offset(
                            stickPosition.x + accumulatedOffset.x,
                            stickPosition.y + accumulatedOffset.y
                        )
                        onDragEnd(finalPos)
                        accumulatedOffset = Offset.Zero
                    },
                    onDragCancel = {
                        accumulatedOffset = Offset.Zero
                    }
                )
            }
    )
}

@Composable
private fun PlacedStickView(
    stick: PlacedStick,
    canvasBounds: Rect,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDoubleTap: () -> Unit,
    isDragging: Boolean
) {
    val color = stickColors[stick.colorIndex % stickColors.size]
    val density = LocalDensity.current

    // Convert screen position to offset from canvas top-left
    val offsetX = stick.x - canvasBounds.left
    val offsetY = stick.y - canvasBounds.top

    val stickWidth = with(density) { 20.dp.toPx() }
    val stickHeight = with(density) { 80.dp.toPx() }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.2f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (offsetX - stickWidth / 2).roundToInt(),
                    (offsetY - stickHeight / 2).roundToInt()
                )
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = if (stick.isHorizontal) 90f else 0f
            }
            .width(20.dp)
            .height(80.dp)
            .shadow(elevation = if (isDragging) 12.dp else 4.dp, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.9f),
                        color,
                        color.copy(alpha = 0.8f)
                    )
                )
            )
            .pointerInput(stick.id) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() }
                )
            }
            .pointerInput(stick.id) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount)
                    },
                    onDragEnd = { onDragEnd() }
                )
            }
    )
}

@Composable
private fun DraggingStickOverlay(
    colorIndex: Int,
    position: Offset,
    isHorizontal: Boolean,
    onRotate: () -> Unit
) {
    val color = stickColors[colorIndex % stickColors.size]
    val density = LocalDensity.current

    val stickWidth = with(density) { 20.dp.toPx() }
    val stickHeight = with(density) { 80.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onRotate() }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (position.x - stickWidth / 2).roundToInt(),
                        (position.y - stickHeight / 2).roundToInt()
                    )
                }
                .graphicsLayer {
                    scaleX = 1.3f
                    scaleY = 1.3f
                    rotationZ = if (isHorizontal) 90f else 0f
                    shadowElevation = 20f
                }
                .width(20.dp)
                .height(80.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            color.copy(alpha = 0.95f),
                            color,
                            color.copy(alpha = 0.9f)
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
private fun CelebrationOverlay(
    targetNumber: Int,
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
                    "You built $targetNumber perfectly!",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                // 7-segment display of the number
                SevenSegmentDisplay(
                    digit = targetNumber,
                    modifier = Modifier.size(80.dp, 120.dp)
                )

                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Stay Here")
                    }

                    if (!isLastLevel) {
                        Button(
                            onClick = onNextLevel,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Next Level")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, "Next")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SevenSegmentDisplay(
    digit: Int,
    modifier: Modifier = Modifier,
    onColor: Color = Color(0xFF4CAF50),
    offColor: Color = Color(0xFFE0E0E0)
) {
    val segments = DigitSegments.getSegmentsForDigit(digit)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = w * 0.12f
        val pad = stroke / 2

        val left = pad
        val right = w - pad
        val top = pad
        val bottom = h - pad
        val mid = h / 2

        // A - Top
        drawLine(
            if (SegmentPosition.A in segments) onColor else offColor,
            Offset(left + stroke, top), Offset(right - stroke, top),
            stroke, StrokeCap.Round
        )
        // B - Top Right
        drawLine(
            if (SegmentPosition.B in segments) onColor else offColor,
            Offset(right, top + stroke), Offset(right, mid - stroke / 2),
            stroke, StrokeCap.Round
        )
        // C - Bottom Right
        drawLine(
            if (SegmentPosition.C in segments) onColor else offColor,
            Offset(right, mid + stroke / 2), Offset(right, bottom - stroke),
            stroke, StrokeCap.Round
        )
        // D - Bottom
        drawLine(
            if (SegmentPosition.D in segments) onColor else offColor,
            Offset(left + stroke, bottom), Offset(right - stroke, bottom),
            stroke, StrokeCap.Round
        )
        // E - Bottom Left
        drawLine(
            if (SegmentPosition.E in segments) onColor else offColor,
            Offset(left, mid + stroke / 2), Offset(left, bottom - stroke),
            stroke, StrokeCap.Round
        )
        // F - Top Left
        drawLine(
            if (SegmentPosition.F in segments) onColor else offColor,
            Offset(left, top + stroke), Offset(left, mid - stroke / 2),
            stroke, StrokeCap.Round
        )
        // G - Middle
        drawLine(
            if (SegmentPosition.G in segments) onColor else offColor,
            Offset(left + stroke, mid), Offset(right - stroke, mid),
            stroke, StrokeCap.Round
        )
    }
}
