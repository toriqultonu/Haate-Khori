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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.alphabettracer.checkDrawingMatch
import com.example.alphabettracer.distanceTo
import com.example.alphabettracer.getLetterPath
import com.example.alphabettracer.model.DrawStroke
import com.example.alphabettracer.model.MatchResult

@Composable
fun TracingCanvas(
    letter: Char,
    modifier: Modifier = Modifier,
    onCheckResult: (MatchResult) -> Unit  // Only called when user clicks Check button
) {
    var isErase by remember { mutableStateOf(false) }
    val availableColors = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFFF44336), // Red
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFF9C27B0), // Purple
        Color(0xFF333333)  // Black
    )
    var currentColor by remember { mutableStateOf(availableColors[0]) }
    val strokes = remember { mutableStateListOf<DrawStroke>() }
    var currentStroke by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }
    var matchResult by remember { mutableStateOf(MatchResult.NONE) }
    var hasChecked by remember { mutableStateOf(false) }  // Track if user has checked
    var strokeWidth by remember { mutableStateOf(18f) }
    var canvasSize by remember { mutableStateOf(0f) }
    var canvasWidth by remember { mutableStateOf(0f) }
    var canvasHeight by remember { mutableStateOf(0f) }
    var showGuide by remember { mutableStateOf(true) }

    // Reset strokes when letter changes
    LaunchedEffect(letter) {
        strokes.clear()
        currentStroke.clear()
        matchResult = MatchResult.NONE
        hasChecked = false
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
                val (bgColor, textColor, message) = when (result) {
                    MatchResult.EXCELLENT -> Triple(Color(0xFF4CAF50), Color.White, "⭐ Excellent! Perfect tracing!")
                    MatchResult.GOOD -> Triple(Color(0xFFFFC107), Color(0xFF333333), "👍 Good job! Almost there!")
                    MatchResult.POOR -> Triple(Color(0xFFFF9800), Color.White, "🔄 Keep trying!")
                    MatchResult.NONE -> Triple(Color(0xFFE0E0E0), Color.Gray, "Draw more of the letter...")
                    null -> Triple(Color(0xFF6200EE).copy(alpha = 0.1f), Color(0xFF6200EE), "✏️ Trace the letter, then tap Check!")
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

                        Spacer(Modifier.width(8.dp))

                        // CHECK BUTTON - prominent button to check drawing
                        Button(
                            onClick = { checkDrawing() },
                            enabled = strokes.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                disabledContainerColor = Color(0xFFE0E0E0)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Check", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    // Color palette
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        availableColors.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (currentColor == color) 3.dp else 0.dp,
                                        color = if (currentColor == color) Color(0xFF333333) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { currentColor = color }
                            )
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
                    if (showGuide) {
                        val letterPath = getLetterPath(letter, canvasSize, canvasWidth, canvasHeight)
                        letterPath.forEach { point ->
                            // Draw guide dots
                            drawCircle(
                                color = Color(0xFF6200EE).copy(alpha = 0.3f),
                                radius = 6f,
                                center = point
                            )
                        }
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