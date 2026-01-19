package com.example.alphabettracer.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import com.example.alphabettracer.ui.components.ConfettiAnimation
import com.example.alphabettracer.ui.components.StickBuilderTutorial
import kotlin.math.abs

// Represents a stick placed on the canvas
data class PlacedStick(
    val id: Int,
    val colorIndex: Int,
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val matchedSegmentId: Int = -1,
    val isHorizontal: Boolean = false
)

// Represents a draggable stick in the tray
data class TrayStick(
    val id: Int,
    val colorIndex: Int,
    val isHorizontal: Boolean = false,
    var isInTray: Boolean = true
)

// All 7 segment positions for ghost display
object AllSevenSegments {
    private const val LEFT = 0.25f
    private const val RIGHT = 0.75f
    private const val TOP = 0.08f
    private const val MIDDLE = 0.48f
    private const val BOTTOM = 0.88f

    val allSegments = listOf(
        StickSegment(LEFT, TOP, RIGHT, TOP, 0),           // A - Top horizontal
        StickSegment(RIGHT, TOP, RIGHT, MIDDLE, 1),       // B - Top-right vertical
        StickSegment(RIGHT, MIDDLE, RIGHT, BOTTOM, 2),    // C - Bottom-right vertical
        StickSegment(LEFT, BOTTOM, RIGHT, BOTTOM, 3),     // D - Bottom horizontal
        StickSegment(LEFT, MIDDLE, LEFT, BOTTOM, 4),      // E - Bottom-left vertical
        StickSegment(LEFT, TOP, LEFT, MIDDLE, 5),         // F - Top-left vertical
        StickSegment(LEFT, MIDDLE, RIGHT, MIDDLE, 6)      // G - Middle horizontal
    )
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
    var showHint by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Tutorial state
    var showTutorial by remember { mutableStateOf(false) }

    // Check if tutorial should be shown on first launch
    LaunchedEffect(Unit) {
        if (!StickBuilderStorage.hasShownTutorial(context)) {
            showTutorial = true
        }
    }

    // Tray sticks - 8 sticks (4 horizontal, 4 vertical)
    val traySticks = remember { mutableStateListOf<TrayStick>() }
    if (traySticks.isEmpty()) {
        repeat(8) { index ->
            traySticks.add(TrayStick(
                id = index,
                colorIndex = index % stickColors.size,
                isHorizontal = index % 2 == 0,
                isInTray = true
            ))
        }
    }

    // Currently dragging stick info
    var draggingStick by remember { mutableStateOf<TrayStick?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var hoveredSegmentId by remember { mutableIntStateOf(-1) }

    // Game board bounds for position calculation
    var boardBounds by remember { mutableStateOf(Rect.Zero) }

    // Helper function to find segment being hovered during drag
    fun findHoveredSegment(offset: Offset, stick: TrayStick): Int {
        if (boardBounds.width <= 0) return -1

        val paddingPx = 32f * context.resources.displayMetrics.density
        val boardContentWidth = boardBounds.width - paddingPx * 2
        val boardContentHeight = boardBounds.height - paddingPx * 2

        val normalizedX = (offset.x - boardBounds.left - paddingPx) / boardContentWidth
        val normalizedY = (offset.y - boardBounds.top - paddingPx) / boardContentHeight

        val segmentThicknessNormalized = 0.12f
        val hitMargin = 0.06f

        AllSevenSegments.allSegments.forEach { segment ->
            val segIsHorizontal = abs(segment.endY - segment.startY) < abs(segment.endX - segment.startX)
            if (segIsHorizontal == stick.isHorizontal) {
                val segLeft: Float
                val segTop: Float
                val segRight: Float
                val segBottom: Float

                if (segIsHorizontal) {
                    segLeft = minOf(segment.startX, segment.endX) - hitMargin
                    segRight = maxOf(segment.startX, segment.endX) + hitMargin
                    segTop = segment.startY - segmentThicknessNormalized / 2 - hitMargin
                    segBottom = segment.startY + segmentThicknessNormalized / 2 + hitMargin
                } else {
                    segLeft = segment.startX - segmentThicknessNormalized / 2 - hitMargin
                    segRight = segment.startX + segmentThicknessNormalized / 2 + hitMargin
                    segTop = minOf(segment.startY, segment.endY) - hitMargin
                    segBottom = maxOf(segment.startY, segment.endY) + hitMargin
                }

                if (normalizedX in segLeft..segRight && normalizedY in segTop..segBottom) {
                    return segment.id
                }
            }
        }
        return -1
    }

    // Reset function
    fun resetLevel() {
        placedSticks.clear()
        showHint = false
        levelCompleted = false
        // Don't reset showConfetti here - let the animation finish naturally
        draggingStick = null
        traySticks.clear()
        repeat(8) { index ->
            traySticks.add(TrayStick(
                id = index,
                colorIndex = index % stickColors.size,
                isHorizontal = index % 2 == 0,
                isInTray = true
            ))
        }
    }

    // Check completion - verify EXACT segments match the target pattern
    fun checkCompletion(): Boolean {
        // Get the segment IDs that the target number requires
        val requiredSegmentIds = targetPattern.map { it.id }.toSet()

        // Get the segment IDs where user placed sticks
        val placedSegmentIds = placedSticks.map { it.matchedSegmentId }.toSet()

        // Check if placed segments exactly match required segments
        return placedSegmentIds == requiredSegmentIds
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
                .padding(12.dp)
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

        Spacer(Modifier.height(8.dp))

        // Main game area
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .onGloballyPositioned { coords ->
                    boardBounds = coords.boundsInRoot()
                },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBE6)),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // All 7 segments as ghost outline + hint highlighting
                SevenSegmentGhostCanvas(
                    allSegments = AllSevenSegments.allSegments,
                    targetPattern = targetPattern,
                    showHint = showHint,
                    hoveredSegmentId = hoveredSegmentId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )

                // Placed sticks
                PlacedSticksCanvas(
                    sticks = placedSticks,
                    onStickTap = { stick ->
                        // Return stick to tray
                        placedSticks.remove(stick)
                        // Find a tray stick with same orientation and mark as in tray
                        val trayStickIndex = traySticks.indexOfFirst {
                            !it.isInTray && it.isHorizontal == stick.isHorizontal
                        }
                        if (trayStickIndex >= 0) {
                            traySticks[trayStickIndex] = traySticks[trayStickIndex].copy(isInTray = true)
                        }
                    },
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
                            color = Color(0xFFFF5722),
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

                // Done button (bottom-right) or Success indicator
                if (levelCompleted) {
                    // Show success indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(Color(0xFF4CAF50), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "✓ Correct!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (checkCompletion()) {
                                levelCompleted = true
                                showConfetti = true  // Trigger confetti celebration!
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
                        Text("CHECK", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Stick tray
        StickTray(
            traySticks = traySticks.filter { it.isInTray },
            draggingStickId = draggingStick?.id,
            onDragStart = { stick, offset ->
                draggingStick = stick
                dragOffset = offset
                hoveredSegmentId = findHoveredSegment(offset, stick)
            },
            onDrag = { offset ->
                dragOffset = offset
                draggingStick?.let { stick ->
                    hoveredSegmentId = findHoveredSegment(offset, stick)
                }
            },
            onDragEnd = { stick, offset, trayTop ->
                // If dropped above tray (on board area), place on board
                if (offset.y < trayTop && boardBounds.width > 0) {
                    // Convert screen position to normalized board coordinates (0-1)
                    // Account for the 32.dp padding inside the card (using density-independent calculation)
                    val paddingPx = 32f * context.resources.displayMetrics.density
                    val boardContentWidth = boardBounds.width - paddingPx * 2
                    val boardContentHeight = boardBounds.height - paddingPx * 2

                    val normalizedX = (offset.x - boardBounds.left - paddingPx) / boardContentWidth
                    val normalizedY = (offset.y - boardBounds.top - paddingPx) / boardContentHeight

                    // Segment thickness in normalized coordinates (same as rendering: 0.12 of width)
                    val segmentThicknessNormalized = 0.12f
                    // Tolerance margin around segment bounds for easier placement
                    val hitMargin = 0.06f

                    // Find the segment that the stick is dropped over (using actual segment bounds)
                    var matchedSegment: StickSegment? = null

                    AllSevenSegments.allSegments.forEach { segment ->
                        val segIsHorizontal = abs(segment.endY - segment.startY) < abs(segment.endX - segment.startX)
                        if (segIsHorizontal == stick.isHorizontal) {
                            // Calculate segment bounds in normalized coordinates (matching rendering logic)
                            val segLeft: Float
                            val segTop: Float
                            val segRight: Float
                            val segBottom: Float

                            if (segIsHorizontal) {
                                segLeft = minOf(segment.startX, segment.endX) - hitMargin
                                segRight = maxOf(segment.startX, segment.endX) + hitMargin
                                segTop = segment.startY - segmentThicknessNormalized / 2 - hitMargin
                                segBottom = segment.startY + segmentThicknessNormalized / 2 + hitMargin
                            } else {
                                segLeft = segment.startX - segmentThicknessNormalized / 2 - hitMargin
                                segRight = segment.startX + segmentThicknessNormalized / 2 + hitMargin
                                segTop = minOf(segment.startY, segment.endY) - hitMargin
                                segBottom = maxOf(segment.startY, segment.endY) + hitMargin
                            }

                            // Check if drop point is within the segment bounds
                            if (normalizedX in segLeft..segRight && normalizedY in segTop..segBottom) {
                                matchedSegment = segment
                            }
                        }
                    }

                    // Place stick if dropped over a valid segment
                    if (matchedSegment != null) {
                        // Mark stick as not in tray
                        val index = traySticks.indexOfFirst { it.id == stick.id }
                        if (index >= 0) {
                            traySticks[index] = stick.copy(isInTray = false)
                        }

                        placedSticks.add(
                            PlacedStick(
                                id = System.currentTimeMillis().toInt(),
                                colorIndex = stick.colorIndex,
                                startX = matchedSegment!!.startX,
                                startY = matchedSegment!!.startY,
                                endX = matchedSegment!!.endX,
                                endY = matchedSegment!!.endY,
                                matchedSegmentId = matchedSegment!!.id,
                                isHorizontal = stick.isHorizontal
                            )
                        )
                    }
                    // If not over any segment, stick returns to tray (stays in tray)
                }
                // If dropped back on tray, stick remains in tray (no change needed)
                hoveredSegmentId = -1
                draggingStick = null
            }
        )

        Spacer(Modifier.height(8.dp))

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
        }

        // Dragging stick overlay (renders on top of everything)
        draggingStick?.let { stick ->
            DraggingStickOverlay(
                stick = stick,
                offset = dragOffset
            )
        }

        // Confetti animation overlay for celebration
        ConfettiAnimation(
            isPlaying = showConfetti,
            onAnimationEnd = { showConfetti = false },
            modifier = Modifier.fillMaxSize()
        )

        // Tutorial overlay for first-time users
        StickBuilderTutorial(
            isVisible = showTutorial,
            onDismiss = {
                showTutorial = false
                StickBuilderStorage.markTutorialShown(context)
            },
            onSkip = {
                showTutorial = false
                StickBuilderStorage.markTutorialShown(context)
            }
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
private fun SevenSegmentGhostCanvas(
    allSegments: List<StickSegment>,
    targetPattern: List<StickSegment>,
    showHint: Boolean,
    hoveredSegmentId: Int = -1,
    modifier: Modifier = Modifier
) {
    // Get target segment IDs for hint highlighting
    val targetSegmentIds = targetPattern.map { it.id }.toSet()

    Canvas(modifier = modifier) {
        // Segment thickness
        val segmentThickness = size.width * 0.12f
        val cornerRadius = segmentThickness / 2

        // Draw all 7 segments as ghost outline
        allSegments.forEach { segment ->
            val startX = segment.startX * size.width
            val startY = segment.startY * size.height
            val endX = segment.endX * size.width
            val endY = segment.endY * size.height

            // Determine if horizontal or vertical segment
            val isHorizontal = abs(endY - startY) < abs(endX - startX)

            val rectLeft: Float
            val rectTop: Float
            val rectWidth: Float
            val rectHeight: Float

            if (isHorizontal) {
                rectLeft = minOf(startX, endX)
                rectTop = startY - segmentThickness / 2
                rectWidth = abs(endX - startX)
                rectHeight = segmentThickness
            } else {
                rectLeft = startX - segmentThickness / 2
                rectTop = minOf(startY, endY)
                rectWidth = segmentThickness
                rectHeight = abs(endY - startY)
            }

            // Check if this segment is being hovered during drag
            val isHovered = segment.id == hoveredSegmentId

            // Draw ghost segment outline (dashed border) for ALL segments
            // Use highlighted color if hovered
            drawRoundRect(
                color = if (isHovered) Color(0xFF2196F3).copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.35f),
                topLeft = Offset(rectLeft, rectTop),
                size = Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(
                    width = if (isHovered) 4f else 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )
            )

            // Draw hover highlight fill (blue) when dragging over segment
            if (isHovered) {
                drawRoundRect(
                    color = Color(0xFF2196F3).copy(alpha = 0.25f),
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }

            // Draw hint fill ONLY for target segments when hint is enabled
            if (showHint && segment.id in targetSegmentIds && !isHovered) {
                val hintColor = Color(0xFF4CAF50) // Green for answer
                drawRoundRect(
                    color = hintColor.copy(alpha = 0.5f),
                    topLeft = Offset(rectLeft, rectTop),
                    size = Size(rectWidth, rectHeight),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }
        }
    }
}

@Composable
private fun PlacedSticksCanvas(
    sticks: List<PlacedStick>,
    onStickTap: (PlacedStick) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Canvas(
        modifier = modifier
            .pointerInput(sticks) {
                detectTapGestures { tapOffset ->
                    // Find which stick was tapped
                    val segmentThickness = canvasSize.width * 0.12f

                    sticks.forEach { stick ->
                        val startX = stick.startX * canvasSize.width
                        val startY = stick.startY * canvasSize.height
                        val endX = stick.endX * canvasSize.width
                        val endY = stick.endY * canvasSize.height
                        val isHorizontal = abs(endY - startY) < abs(endX - startX)

                        val rectLeft = if (isHorizontal) minOf(startX, endX) else startX - segmentThickness / 2
                        val rectTop = if (isHorizontal) startY - segmentThickness / 2 else minOf(startY, endY)
                        val rectRight = if (isHorizontal) maxOf(startX, endX) else startX + segmentThickness / 2
                        val rectBottom = if (isHorizontal) startY + segmentThickness / 2 else maxOf(startY, endY)

                        if (tapOffset.x in rectLeft..rectRight && tapOffset.y in rectTop..rectBottom) {
                            onStickTap(stick)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        canvasSize = size

        // Same segment thickness as outline
        val segmentThickness = size.width * 0.12f
        val cornerRadius = segmentThickness / 2

        sticks.forEach { stick ->
            val color = stickColors[stick.colorIndex % stickColors.size]

            val startX = stick.startX * size.width
            val startY = stick.startY * size.height
            val endX = stick.endX * size.width
            val endY = stick.endY * size.height

            // Determine if horizontal or vertical segment
            val isHorizontal = abs(endY - startY) < abs(endX - startX)

            val rectLeft: Float
            val rectTop: Float
            val rectWidth: Float
            val rectHeight: Float

            if (isHorizontal) {
                rectLeft = minOf(startX, endX)
                rectTop = startY - segmentThickness / 2
                rectWidth = abs(endX - startX)
                rectHeight = segmentThickness
            } else {
                rectLeft = startX - segmentThickness / 2
                rectTop = minOf(startY, endY)
                rectWidth = segmentThickness
                rectHeight = abs(endY - startY)
            }

            // Draw filled segment
            drawRoundRect(
                color = color,
                topLeft = Offset(rectLeft, rectTop),
                size = Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )

            // Draw inner highlight for 3D effect
            drawRoundRect(
                color = color.copy(alpha = 0.7f),
                topLeft = Offset(rectLeft + 4f, rectTop + 4f),
                size = Size(rectWidth - 8f, rectHeight - 8f),
                cornerRadius = CornerRadius(cornerRadius - 2f, cornerRadius - 2f)
            )
        }
    }
}

@Composable
private fun StickTray(
    traySticks: List<TrayStick>,
    draggingStickId: Int?,
    onDragStart: (TrayStick, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (TrayStick, Offset, Float) -> Unit
) {
    var trayTopY by remember { mutableFloatStateOf(0f) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coords ->
                trayTopY = coords.boundsInRoot().top
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD7CCC8)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(12.dp)
        ) {
            // Inner tray with sticks
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
                        TrayStickItem(
                            stick = stick,
                            isDragging = stick.id == draggingStickId,
                            onDragStart = { offset -> onDragStart(stick, offset) },
                            onDrag = onDrag,
                            onDragEnd = { offset -> onDragEnd(stick, offset, trayTopY) }
                        )
                    }

                    // Show placeholder if tray is empty
                    if (traySticks.isEmpty()) {
                        Text(
                            "Tap sticks on board to return",
                            color = Color(0xFF5D4037),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrayStickItem(
    stick: TrayStick,
    isDragging: Boolean,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: (Offset) -> Unit
) {
    val color = stickColors[stick.colorIndex % stickColors.size]
    var itemBounds by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                itemBounds = coords.boundsInRoot()
            }
            .then(
                if (stick.isHorizontal) {
                    Modifier.width(50.dp).height(16.dp)
                } else {
                    Modifier.width(16.dp).height(50.dp)
                }
            )
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = 0.9f),
                        color,
                        color.copy(alpha = 0.9f)
                    )
                )
            )
            .pointerInput(stick.id) {
                var dragPosition = Offset.Zero
                detectDragGestures(
                    onDragStart = { startOffset ->
                        // Calculate absolute position from item bounds + touch offset
                        dragPosition = Offset(
                            itemBounds.left + startOffset.x,
                            itemBounds.top + startOffset.y
                        )
                        onDragStart(dragPosition)
                    },
                    onDragEnd = {
                        onDragEnd(dragPosition)
                    },
                    onDragCancel = {
                        onDragEnd(dragPosition)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragPosition = Offset(
                            dragPosition.x + dragAmount.x,
                            dragPosition.y + dragAmount.y
                        )
                        onDrag(dragPosition)
                    }
                )
            }
            .graphicsLayer {
                alpha = if (isDragging) 0f else 1f
            }
    )
}

@Composable
private fun DraggingStickOverlay(
    stick: TrayStick,
    offset: Offset
) {
    val color = stickColors[stick.colorIndex % stickColors.size]
    var canvasPosition by remember { mutableStateOf(Offset.Zero) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                canvasPosition = coords.positionInRoot()
            }
    ) {
        // Adjust offset to be relative to this canvas's coordinate system
        val adjustedOffset = Offset(
            offset.x - canvasPosition.x,
            offset.y - canvasPosition.y
        )

        val stickWidth = if (stick.isHorizontal) 140f else 45f
        val stickHeight = if (stick.isHorizontal) 45f else 140f
        val cornerRadius = 22f

        // Shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.3f),
            topLeft = Offset(adjustedOffset.x - stickWidth / 2 + 4, adjustedOffset.y - stickHeight / 2 + 4),
            size = Size(stickWidth, stickHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // Main stick
        drawRoundRect(
            color = color,
            topLeft = Offset(adjustedOffset.x - stickWidth / 2, adjustedOffset.y - stickHeight / 2),
            size = Size(stickWidth, stickHeight),
            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
        )

        // Highlight
        drawRoundRect(
            color = color.copy(alpha = 0.7f),
            topLeft = Offset(adjustedOffset.x - stickWidth / 2 + 4, adjustedOffset.y - stickHeight / 2 + 4),
            size = Size(stickWidth - 8, stickHeight - 8),
            cornerRadius = CornerRadius(cornerRadius - 2, cornerRadius - 2)
        )
    }
}

