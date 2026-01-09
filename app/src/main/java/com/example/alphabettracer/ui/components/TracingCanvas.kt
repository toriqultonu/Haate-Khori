package com.example.alphabettracer.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.alphabettracer.data.LetterStorage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.model.DrawStroke
import com.example.alphabettracer.model.MatchResult
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun TracingCanvas(
    letter: Char,
    modifier: Modifier = Modifier,
    onColorSelected: (Int) -> Unit = {},  // Called when user selects a color
    onCheckResult: (MatchResult) -> Unit  // Only called when user clicks Check button
) {
    val context = LocalContext.current
    var isErase by remember { mutableStateOf(false) }
    val availableColors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFFF44336), // Red
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF333333)  // Black
    )

    // Load saved color index from storage
    val savedColorIndex = remember { LetterStorage.getSelectedColor(context) }
    var currentColor by remember { mutableStateOf(availableColors[savedColorIndex.coerceIn(0, availableColors.lastIndex)]) }
    val strokes = remember { mutableStateListOf<DrawStroke>() }
    var currentStroke by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }
    var matchResult by remember { mutableStateOf(MatchResult.NONE) }
    var hasChecked by remember { mutableStateOf(false) }  // Track if user has checked

    // Load saved stroke width from storage
    var strokeWidth by remember { mutableStateOf(LetterStorage.getStrokeWidth(context)) }
    var canvasSize by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }
    var showGuide by remember { mutableStateOf(true) }
    var isPlayingDemo by remember { mutableStateOf(false) }
    var demoProgress by remember { mutableStateOf(0f) }
    var showColorPicker by remember { mutableStateOf(false) }

    // Color names for the picker
    val colorNames = listOf("Blue", "Red", "Green", "Orange", "Purple", "Black")

    // Animate the demo progress
    LaunchedEffect(isPlayingDemo) {
        if (isPlayingDemo) {
            demoProgress = 0f
            val startTime = System.currentTimeMillis()
            val duration = 3000L // 3 seconds for full animation
            while (System.currentTimeMillis() - startTime < duration && isPlayingDemo) {
                demoProgress = ((System.currentTimeMillis() - startTime).toFloat() / duration).coerceIn(0f, 1f)
                kotlinx.coroutines.delay(16) // ~60fps
            }
            if (isPlayingDemo) {
                demoProgress = 1f
                kotlinx.coroutines.delay(500) // Pause at end
                isPlayingDemo = false
                demoProgress = 0f
            }
        }
    }

    // Reset strokes when letter changes
    LaunchedEffect(letter) {
        strokes.clear()
        currentStroke.clear()
        matchResult = MatchResult.NONE
        hasChecked = false
        isPlayingDemo = false
        demoProgress = 0f
    }

    // Function to check the drawing (called when user clicks Check button)
    fun checkDrawing() {
        if (canvasSize > 0f && canvasWidth > 0f && strokes.isNotEmpty()) {
            val letterPath = getLetterPath(letter, canvasSize, canvasWidth, canvasHeight)
            matchResult = checkDrawingMatch(strokes.toList(), letterPath, canvasSize)
            hasChecked = true
            onCheckResult(matchResult)
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Feedback bar - only shows result after checking
            AnimatedContent(
                targetState = if (hasChecked) matchResult else null,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(200))
                },
                label = "feedback"
            ) { result ->
                // Randomized encouraging messages for kids
                val excellentMessages = listOf(
                    "⭐ WOW! Perfect tracing!",
                    "⭐ AMAZING! You're a star!",
                    "⭐ SUPER! You nailed it!",
                    "⭐ FANTASTIC! Great work!",
                    "⭐ BRILLIANT! Keep shining!",
                    "⭐ AWESOME! You did it!"
                )
                val goodMessages = listOf(
                    "👍 Good job! Almost there!",
                    "👍 Nice try! So close!",
                    "👍 Great effort! Try again!",
                    "👍 You're getting better!",
                    "👍 Keep going! You got this!"
                )
                val poorMessages = listOf(
                    "🔄 Keep trying! You can do it!",
                    "🔄 Don't give up! Try again!",
                    "🔄 Practice makes perfect!",
                    "🔄 Almost! Trace the whole letter!",
                    "🔄 You're learning! Keep going!"
                )
                val noneMessages = listOf(
                    "Draw more of the letter...",
                    "Trace the dotted path...",
                    "Follow the guide dots..."
                )
                val promptMessages = listOf(
                    "✏️ Trace the letter, then tap Check!",
                    "✏️ Follow the dots and draw!",
                    "✏️ Ready? Start tracing!"
                )

                val (bgColor, textColor, message) = when (result) {
                    MatchResult.EXCELLENT -> Triple(Color(0xFF4CAF50), Color.White, excellentMessages.random())
                    MatchResult.GOOD -> Triple(Color(0xFFFFC107), Color(0xFF333333), goodMessages.random())
                    MatchResult.POOR -> Triple(Color(0xFFFF9800), Color.White, poorMessages.random())
                    MatchResult.NONE -> Triple(Color(0xFFE0E0E0), Color.Gray, noneMessages.random())
                    null -> Triple(Color(0xFF6200EE).copy(alpha = 0.1f), Color(0xFF6200EE), promptMessages.random())
                }
                Surface(
                    color = bgColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Toolbar
            Surface(
                color = Color(0xFFFAFAFA),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Drawing tools
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Pencil/Eraser toggle
                        FilledIconToggleButton(
                            checked = !isErase,
                            onCheckedChange = { isErase = !it },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (!isErase) Icons.Default.Edit else Icons.Default.Delete,
                                contentDescription = if (!isErase) "Pencil" else "Eraser",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        // Clear button
                        IconButton(
                            onClick = {
                                strokes.clear()
                                currentStroke.clear()
                                matchResult = MatchResult.NONE
                                hasChecked = false
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Default.Delete, "Clear", modifier = Modifier.size(20.dp))
                        }

                        Spacer(Modifier.width(4.dp))

                        // Show/hide guide toggle
                        IconButton(
                            onClick = { showGuide = !showGuide },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                if (showGuide) Icons.Sharp.AddCircle else Icons.Sharp.Clear,
                                "Toggle guide",
                                modifier = Modifier.size(20.dp),
                                tint = if (showGuide) Color(0xFF6200EE) else Color.Gray
                            )
                        }

                        // Undo button
                        IconButton(
                            onClick = {
                                if (strokes.isNotEmpty()) {
                                    strokes.removeAt(strokes.lastIndex)
                                    hasChecked = false  // Reset check status when undoing
                                }
                            },
                            enabled = strokes.isNotEmpty(),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                "Undo",
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        // SHOW ME BUTTON - animated demo
                        Button(
                            onClick = { isPlayingDemo = !isPlayingDemo },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingDemo) Color(0xFFFF9800) else Color(0xFF2196F3)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                if (isPlayingDemo) Icons.Default.Star else Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
//                            Spacer(Modifier.width(2.dp))
//                            Text(
//                                if (isPlayingDemo) "Stop" else "Demo",
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 12.sp
//                            )
                        }

                        Spacer(Modifier.width(4.dp))

                        // CHECK BUTTON - prominent button to check drawing
                        Button(
                            onClick = { checkDrawing() },
                            enabled = strokes.isNotEmpty() && !isPlayingDemo,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text("Check", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    // Compact Color Picker with Dropdown
                    Box {
                        // Current color button
                        Surface(
                            modifier = Modifier
                                .clickable { showColorPicker = true }
                                .padding(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(currentColor)
                                        .border(2.dp, Color(0xFF333333), CircleShape)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "▼",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        // Dropdown menu
                        DropdownMenu(
                            expanded = showColorPicker,
                            onDismissRequest = { showColorPicker = false }
                        ) {
                            availableColors.forEachIndexed { index, color ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(color)
                                                    .border(
                                                        width = if (currentColor == color) 2.dp else 1.dp,
                                                        color = if (currentColor == color) Color(0xFF333333) else Color.Gray,
                                                        shape = CircleShape
                                                    )
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Text(
                                                colorNames[index],
                                                fontWeight = if (currentColor == color) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (currentColor == color) {
                                                Spacer(Modifier.width(8.dp))
                                                Text("✓", color = Color(0xFF4CAF50))
                                            }
                                        }
                                    },
                                    onClick = {
                                        currentColor = color
                                        LetterStorage.saveSelectedColor(context, index)  // Save color preference
                                        onColorSelected(index)
                                        showColorPicker = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Stroke width slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Info,
                    "Stroke width",
                    modifier = Modifier.size(18.dp),
                    tint = Color.Gray
                )
                Slider(
                    value = strokeWidth,
                    onValueChange = { strokeWidth = it },
                    onValueChangeFinished = {
                        LetterStorage.saveStrokeWidth(context, strokeWidth)  // Save stroke width preference
                    },
                    valueRange = 8f..35f,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF6200EE),
                        activeTrackColor = Color(0xFF6200EE)
                    )
                )
                Box(
                    modifier = Modifier
                        .size(strokeWidth.dp.coerceAtMost(30.dp))
                        .background(currentColor, CircleShape)
                )
            }

            // Drawing canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (hasChecked) {
                            when (matchResult) {
                                MatchResult.EXCELLENT -> Color(0xFF4CAF50).copy(alpha = 0.12f)
                                MatchResult.GOOD -> Color(0xFFFFC107).copy(alpha = 0.12f)
                                MatchResult.POOR -> Color(0xFFFF9800).copy(alpha = 0.12f)
                                MatchResult.NONE -> Color(0xFFF5F5F5)
                            }
                        } else {
                            Color(0xFFF5F5F5)  // Neutral background while drawing
                        }
                    )
                    .border(
                        width = 2.dp,
                        color = if (hasChecked && matchResult == MatchResult.EXCELLENT)
                            Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .pointerInput(letter, isErase, currentColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (!isErase) {
                                    currentStroke = mutableListOf(offset)
                                    // Reset check status when user starts drawing again
                                    if (hasChecked) {
                                        hasChecked = false
                                        matchResult = MatchResult.NONE
                                    }
                                }
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                if (!isErase) {
                                    currentStroke = (currentStroke + change.position).toMutableList()
                                } else {
                                    val eraserRadius = 40f
                                    val toRemove = strokes.filter { stroke ->
                                        stroke.points.any { point ->
                                            point.distanceTo(change.position) < eraserRadius
                                        }
                                    }
                                    if (toRemove.isNotEmpty()) {
                                        strokes.removeAll(toRemove)
                                        hasChecked = false  // Reset when erasing
                                    }
                                }
                            },
                            onDragEnd = {
                                if (!isErase && currentStroke.size > 1) {
                                    strokes.add(DrawStroke(currentStroke.toList(), currentColor, strokeWidth))
                                }
                                currentStroke = mutableListOf()
                            },
                            onDragCancel = {
                                currentStroke = mutableListOf()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    canvasSize = size.minDimension
                    canvasWidth = size.width
                    canvasHeight = size.height

                    // Draw guide path (dots showing where to trace)
                    val letterPath = getLetterPath(letter, canvasSize, canvasWidth, canvasHeight)
                    if (showGuide) {
                        letterPath.forEach { point ->
                            // Draw guide dots
                            drawCircle(
                                color = Color(0xFF6200EE).copy(alpha = 0.3f),
                                radius = 6f,
                                center = point
                            )
                        }
                    }

                    // Draw animated demo
                    if (isPlayingDemo && letterPath.isNotEmpty()) {
                        val currentPointIndex = (demoProgress * (letterPath.size - 1)).toInt()
                        val trailLength = 30 // Number of trailing points

                        // Draw the trail (fading line behind the dot)
                        val trailStart = maxOf(0, currentPointIndex - trailLength)
                        if (currentPointIndex > trailStart) {
                            val trailPath = Path().apply {
                                for (i in trailStart..currentPointIndex) {
                                    val point = letterPath[i]
                                    if (i == trailStart) moveTo(point.x, point.y)
                                    else lineTo(point.x, point.y)
                                }
                            }
                            drawPath(
                                path = trailPath,
                                color = Color(0xFFFF5722),
                                style = Stroke(
                                    width = 12f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        // Draw the animated dot (current position)
                        val currentPoint = letterPath[currentPointIndex]
                        // Outer glow
                        drawCircle(
                            color = Color(0xFFFF5722).copy(alpha = 0.3f),
                            radius = 24f,
                            center = currentPoint
                        )
                        // Inner dot
                        drawCircle(
                            color = Color(0xFFFF5722),
                            radius = 14f,
                            center = currentPoint
                        )
                        // White center
                        drawCircle(
                            color = Color.White,
                            radius = 6f,
                            center = currentPoint
                        )
                    }

                    // Draw faded letter background
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            alpha = (255 * 0.15).toInt()
                            textSize = canvasSize * 0.75f
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(
                            letter.toString(),
                            size.width / 2,
                            size.height / 2 + paint.textSize / 3,
                            paint
                        )
                    }

                    // Draw completed strokes
                    strokes.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                stroke.points.forEachIndexed { index, offset ->
                                    if (index == 0) moveTo(offset.x, offset.y)
                                    else lineTo(offset.x, offset.y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.width,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw current stroke
                    if (currentStroke.size > 1) {
                        val path = Path().apply {
                            currentStroke.forEachIndexed { index, offset ->
                                if (index == 0) moveTo(offset.x, offset.y)
                                else lineTo(offset.x, offset.y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = if (isErase) Color.Red.copy(alpha = 0.4f) else currentColor,
                            style = Stroke(
                                width = if (isErase) 50f else strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }
            }
        }
    }
}

fun getLetterPath(letter: Char, size: Float, canvasWidth: Float = size, canvasHeight: Float = size): List<Offset> {
    // Center the letter in the actual canvas, not in a square
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2
    val scale = size / 300f

    // Helper function to interpolate points between two offsets
    fun interpolate(start: Offset, end: Offset, steps: Int): List<Offset> {
        return (0..steps).map { i ->
            val t = i.toFloat() / steps
            Offset(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t
            )
        }
    }

    // Helper function to create arc points
    fun arcPoints(cx: Float, cy: Float, radius: Float, startAngle: Float, endAngle: Float, steps: Int): List<Offset> {
        return (0..steps).map { i ->
            val angle = startAngle + (endAngle - startAngle) * i / steps
            Offset(
                cx + radius * cos(Math.toRadians(angle.toDouble()).toFloat()),
                cy + radius * sin(Math.toRadians(angle.toDouble()).toFloat())
            )
        }
    }

    return when (letter.uppercaseChar()) {
        'A' -> {
            val leftLeg = interpolate(
                Offset(centerX - 70 * scale, centerY + 90 * scale),
                Offset(centerX, centerY - 90 * scale),
                20
            )
            val rightLeg = interpolate(
                Offset(centerX, centerY - 90 * scale),
                Offset(centerX + 70 * scale, centerY + 90 * scale),
                20
            )
            val crossBar = interpolate(
                Offset(centerX - 40 * scale, centerY + 10 * scale),
                Offset(centerX + 40 * scale, centerY + 10 * scale),
                15
            )
            leftLeg + rightLeg + crossBar
        }
        'B' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val topBump = arcPoints(centerX - 50 * scale, centerY - 45 * scale, 45 * scale, -90f, 90f, 15)
            val bottomBump = arcPoints(centerX - 50 * scale, centerY + 45 * scale, 45 * scale, -90f, 90f, 15)
            stem + topBump + bottomBump
        }
        'C' -> {
            // The background 'C' is roughly a circle centered in the box.
            // We use a radius that matches the visual background (approx 85 units at 300 scale)
            val radius = 80 * scale
            arcPoints(
                cx = centerX,
                cy = centerY,
                radius = radius,
                startAngle = 45f,
                endAngle = 315f,
                steps = 40
            )
        }        'D' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val curve = arcPoints(centerX - 50 * scale, centerY, 90 * scale, -90f, 90f, 25)
            stem + curve
        }
        'E' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val top = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                15
            )
            val middle = interpolate(
                Offset(centerX - 50 * scale, centerY),
                Offset(centerX + 40 * scale, centerY),
                12
            )
            val bottom = interpolate(
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                15
            )
            stem + top + middle + bottom
        }
        'F' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val top = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                15
            )
            val middle = interpolate(
                Offset(centerX - 50 * scale, centerY - 10 * scale),
                Offset(centerX + 40 * scale, centerY - 10 * scale),
                12
            )
            stem + top + middle
        }
        'G' -> {
            val radius = 85 * scale

            // 1. The main curve of the G (similar to C, but ends a bit later)
            // Starts at top-right (330°) and sweeps around to the middle-right (20°)
            val curve = arcPoints(
                cx = centerX,
                cy = centerY,
                radius = radius,
                startAngle = 330f,
                endAngle = 30f,
                steps = 40
            )

            // 2. The horizontal crossbar
            // We start from where the curve ends (approx) and move toward the center
            val barStart = Offset(centerX + radius, centerY + 20 * scale)
            val barEnd = Offset(centerX + 20 * scale, centerY + 20 * scale)
            val crossBar = interpolate(barStart, barEnd, 10)

            curve + crossBar
        }
        'H' -> {
            val leftStem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val rightStem = interpolate(
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                25
            )
            val crossBar = interpolate(
                Offset(centerX - 50 * scale, centerY),
                Offset(centerX + 50 * scale, centerY),
                15
            )
            leftStem + rightStem + crossBar
        }
        'I' -> {
            val stem = interpolate(
                Offset(centerX, centerY - 90 * scale),
                Offset(centerX, centerY + 90 * scale),
                25
            )
            val top = interpolate(
                Offset(centerX - 40 * scale, centerY - 90 * scale),
                Offset(centerX + 40 * scale, centerY - 90 * scale),
                12
            )
            val bottom = interpolate(
                Offset(centerX - 40 * scale, centerY + 90 * scale),
                Offset(centerX + 40 * scale, centerY + 90 * scale),
                12
            )
            stem + top + bottom
        }
        'J' -> {
            val stem = interpolate(
                Offset(centerX + 30 * scale, centerY - 90 * scale),
                Offset(centerX + 30 * scale, centerY + 40 * scale),
                20
            )
            val hook = arcPoints(centerX - 20 * scale, centerY + 40 * scale, 50 * scale, 0f, 180f, 15)
            val top = interpolate(
                Offset(centerX - 20 * scale, centerY - 90 * scale),
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                12
            )
            stem + hook + top
        }
        'K' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val upperDiag = interpolate(
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY),
                20
            )
            val lowerDiag = interpolate(
                Offset(centerX - 50 * scale, centerY),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                20
            )
            stem + upperDiag + lowerDiag
        }
        'L' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val bottom = interpolate(
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                15
            )
            stem + bottom
        }
        'M' -> {
            val leftStem = interpolate(
                Offset(centerX - 60 * scale, centerY + 90 * scale),
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                20
            )
            val leftDiag = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX, centerY + 20 * scale),
                15
            )
            val rightDiag = interpolate(
                Offset(centerX, centerY + 20 * scale),
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                15
            )
            val rightStem = interpolate(
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                Offset(centerX + 60 * scale, centerY + 90 * scale),
                20
            )
            leftStem + leftDiag + rightDiag + rightStem
        }
        'N' -> {
            val leftStem = interpolate(
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                20
            )
            val diagonal = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                25
            )
            val rightStem = interpolate(
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                20
            )
            leftStem + diagonal + rightStem
        }
        'O' -> {
            arcPoints(centerX, centerY, 80 * scale, 0f, 360f, 40)
        }
        'P' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val bump = arcPoints(centerX - 50 * scale, centerY - 35 * scale, 55 * scale, -90f, 90f, 20)
            stem + bump
        }
        'Q' -> {
            val circle = arcPoints(centerX, centerY - 10 * scale, 75 * scale, 0f, 360f, 40)
            val tail = interpolate(
                Offset(centerX + 20 * scale, centerY + 40 * scale),
                Offset(centerX + 70 * scale, centerY + 90 * scale),
                12
            )
            circle + tail
        }
        'R' -> {
            val stem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 90 * scale),
                25
            )
            val bump = arcPoints(centerX - 50 * scale, centerY - 35 * scale, 55 * scale, -90f, 90f, 20)
            val leg = interpolate(
                Offset(centerX - 50 * scale, centerY + 20 * scale),
                Offset(centerX + 50 * scale, centerY + 90 * scale),
                18
            )
            stem + bump + leg
        }
        'S' -> {
            // Note: scale is already calculated as size / 300f in the function
            // centerX and centerY are already defined

            // Adjusted parameters for better alignment
            val topRadius = 45 * scale
            val bottomRadius = 45 * scale

            // Top curve center - moved slightly right and up
            val topCenterY = centerY - 45 * scale
            val topCenterX = centerX + 5 * scale  // Slight right shift

            // Bottom curve center - moved slightly left and down
            val bottomCenterY = centerY + 45 * scale
            val bottomCenterX = centerX - 5 * scale  // Slight left shift

            // Top curve: starts from top-left, curves to right
            // Angle adjustments: start from 170° (top-left) to -20° (middle-right)
            val topCurve = arcPoints(
                cx = topCenterX,
                cy = topCenterY,
                radius = topRadius,
                startAngle = 20f,
                endAngle = -170f,
                steps = 28
            )

            // Bottom curve: continues from middle, curves to bottom-right
            // Angle adjustments: start from 160° (middle-left) to -10° (bottom-right)
            val bottomCurve = arcPoints(
                cx = bottomCenterX,
                cy = bottomCenterY,
                radius = bottomRadius,
                startAngle = -10f,
                endAngle = 160f,
                steps = 28
            )

            topCurve + bottomCurve
        }

        'T' -> {
            val stem = interpolate(
                Offset(centerX, centerY - 90 * scale),
                Offset(centerX, centerY + 90 * scale),
                25
            )
            val top = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                18
            )
            stem + top
        }
        'U' -> {
            val leftStem = interpolate(
                Offset(centerX - 50 * scale, centerY - 90 * scale),
                Offset(centerX - 50 * scale, centerY + 30 * scale),
                18
            )
            val curve = arcPoints(centerX, centerY + 30 * scale, 50 * scale, -180f, -360f, 20)
            val rightStem = interpolate(
                Offset(centerX + 50 * scale, centerY + 30 * scale),
                Offset(centerX + 50 * scale, centerY - 90 * scale),
                18
            )
            leftStem + curve + rightStem
        }
        'V' -> {
            val leftLeg = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX, centerY + 90 * scale),
                25
            )
            val rightLeg = interpolate(
                Offset(centerX, centerY + 90 * scale),
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                25
            )
            leftLeg + rightLeg
        }
        'W' -> {
            val leg1 = interpolate(
                Offset(centerX - 70 * scale, centerY - 90 * scale),
                Offset(centerX - 35 * scale, centerY + 90 * scale),
                18
            )
            val leg2 = interpolate(
                Offset(centerX - 35 * scale, centerY + 90 * scale),
                Offset(centerX, centerY - 20 * scale),
                15
            )
            val leg3 = interpolate(
                Offset(centerX, centerY - 20 * scale),
                Offset(centerX + 35 * scale, centerY + 90 * scale),
                15
            )
            val leg4 = interpolate(
                Offset(centerX + 35 * scale, centerY + 90 * scale),
                Offset(centerX + 70 * scale, centerY - 90 * scale),
                18
            )
            leg1 + leg2 + leg3 + leg4
        }
        'X' -> {
            val diag1 = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX + 60 * scale, centerY + 90 * scale),
                30
            )
            val diag2 = interpolate(
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                Offset(centerX - 60 * scale, centerY + 90 * scale),
                30
            )
            diag1 + diag2
        }
        'Y' -> {
            val leftArm = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX, centerY),
                18
            )
            val rightArm = interpolate(
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                Offset(centerX, centerY),
                18
            )
            val stem = interpolate(
                Offset(centerX, centerY),
                Offset(centerX, centerY + 90 * scale),
                15
            )
            leftArm + rightArm + stem
        }
        'Z' -> {
            val top = interpolate(
                Offset(centerX - 60 * scale, centerY - 90 * scale),
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                18
            )
            val diagonal = interpolate(
                Offset(centerX + 60 * scale, centerY - 90 * scale),
                Offset(centerX - 60 * scale, centerY + 90 * scale),
                30
            )
            val bottom = interpolate(
                Offset(centerX - 60 * scale, centerY + 90 * scale),
                Offset(centerX + 60 * scale, centerY + 90 * scale),
                18
            )
            top + diagonal + bottom
        }
        else -> emptyList()
    }
}

fun checkDrawingMatch(
    userStrokes: List<DrawStroke>,
    letterPath: List<Offset>,
    canvasSize: Float
): MatchResult {
    if (userStrokes.isEmpty()) return MatchResult.NONE
    val allUserPoints = userStrokes.flatMap { it.points }
    if (allUserPoints.size < 10) return MatchResult.NONE  // Need minimum points
    if (letterPath.isEmpty()) return MatchResult.NONE

    // Reasonable tolerance - 10% of canvas size
    val tolerance = canvasSize * 0.10f

    // Calculate coverage: how much of the letter path is covered by user strokes
    var coveredPoints = 0
    letterPath.forEach { pathPoint ->
        val isNearUserStroke = allUserPoints.any { userPoint ->
            val distance = sqrt(
                (pathPoint.x - userPoint.x) * (pathPoint.x - userPoint.x) +
                        (pathPoint.y - userPoint.y) * (pathPoint.y - userPoint.y)
            )
            distance < tolerance
        }
        if (isNearUserStroke) coveredPoints++
    }
    val coverage = coveredPoints.toFloat() / letterPath.size

    // Calculate accuracy: how much of user's drawing is on the letter path
    // This prevents random scribbles from passing
    var accuratePoints = 0
    allUserPoints.forEach { userPoint ->
        val isNearPath = letterPath.any { pathPoint ->
            val distance = sqrt(
                (pathPoint.x - userPoint.x) * (pathPoint.x - userPoint.x) +
                        (pathPoint.y - userPoint.y) * (pathPoint.y - userPoint.y)
            )
            distance < tolerance
        }
        if (isNearPath) accuratePoints++
    }
    val accuracy = if (allUserPoints.isNotEmpty()) accuratePoints.toFloat() / allUserPoints.size else 0f

    // Both coverage AND accuracy matter equally
    // Coverage: did you trace the whole letter?
    // Accuracy: did you stay on the letter path?
    val score = (coverage * 0.5f) + (accuracy * 0.5f)

    // Minimum coverage requirement - must trace at least 40% of the letter
    if (coverage < 0.40f) {
        return if (coverage > 0.20f) MatchResult.POOR else MatchResult.NONE
    }

    // Minimum accuracy requirement - at least 30% of strokes should be on the path
    if (accuracy < 0.30f) {
        return MatchResult.POOR
    }

    return when {
        score > 0.70f -> MatchResult.EXCELLENT  // Need 70%+ for excellent
        score > 0.55f -> MatchResult.GOOD       // Need 55%+ for good
        score > 0.35f -> MatchResult.POOR       // 35%+ is poor but attempted
        else -> MatchResult.NONE
    }
}

fun Offset.distanceTo(other: Offset): Float {
    return sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y))
}