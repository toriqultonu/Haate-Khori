package com.example.alphabettracer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Data class to hold a drawing stroke with its points, color and stroke width.
 * Using a list of Offset points allows for smooth path rendering.
 */
data class DrawingPath(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

/**
 * A coloring canvas that allows free-form drawing with touch gestures.
 * Kids can color shapes by drawing with their finger like using a pen.
 * Uses point-based drawing for smooth strokes (same technique as TracingCanvas).
 */
@Composable
fun ColoringCanvas(
    shapeId: String,
    selectedColor: Color,
    brushSize: Float,
    isEraser: Boolean,
    drawingPaths: MutableList<DrawingPath>,
    modifier: Modifier = Modifier
) {
    // Current stroke being drawn - stored as list of points for smoothness
    var currentStroke by remember { mutableStateOf<MutableList<Offset>>(mutableListOf()) }
    var currentColor by remember(selectedColor) { mutableStateOf(selectedColor) }
    var currentStrokeWidth by remember(brushSize) { mutableStateOf(brushSize) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(2.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(selectedColor, brushSize, isEraser) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Start a new stroke with the initial point
                            currentStroke = mutableListOf(offset)
                            currentColor = if (isEraser) Color.White else selectedColor
                            currentStrokeWidth = if (isEraser) brushSize * 2 else brushSize
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            // Add each point to the current stroke for smooth drawing
                            currentStroke = (currentStroke + change.position).toMutableList()
                        },
                        onDragEnd = {
                            // Save the completed stroke if it has enough points
                            if (currentStroke.size > 1) {
                                drawingPaths.add(
                                    DrawingPath(
                                        points = currentStroke.toList(),
                                        color = currentColor,
                                        strokeWidth = currentStrokeWidth
                                    )
                                )
                            }
                            currentStroke = mutableListOf()
                        },
                        onDragCancel = {
                            currentStroke = mutableListOf()
                        }
                    )
                }
        ) {
            // Draw all completed strokes (user's coloring)
            drawingPaths.forEach { drawingPath ->
                if (drawingPath.points.size > 1) {
                    val path = Path().apply {
                        drawingPath.points.forEachIndexed { index, offset ->
                            if (index == 0) moveTo(offset.x, offset.y)
                            else lineTo(offset.x, offset.y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = drawingPath.color,
                        style = Stroke(
                            width = drawingPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // Draw current stroke being drawn (for real-time feedback)
            if (currentStroke.size > 1) {
                val path = Path().apply {
                    currentStroke.forEachIndexed { index, offset ->
                        if (index == 0) moveTo(offset.x, offset.y)
                        else lineTo(offset.x, offset.y)
                    }
                }
                drawPath(
                    path = path,
                    color = currentColor,
                    style = Stroke(
                        width = currentStrokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw the shape outline on top
            drawShapeOutline(shapeId)
        }
    }
}

/**
 * Draw the shape outline that kids will color inside
 */
private fun DrawScope.drawShapeOutline(shapeId: String) {
    val cx = size.width / 2
    val cy = size.height / 2
    val scale = min(size.width, size.height) / 400f
    val outlineColor = Color(0xFF333333)
    val outlineStroke = Stroke(width = 4f)

    when (shapeId) {
        "star" -> drawStarOutline(cx, cy, scale, outlineColor, outlineStroke)
        "heart" -> drawHeartOutline(cx, cy, scale, outlineColor, outlineStroke)
        "sun" -> drawSunOutline(cx, cy, scale, outlineColor, outlineStroke)
        "flower" -> drawFlowerOutline(cx, cy, scale, outlineColor, outlineStroke)
        "butterfly" -> drawButterflyOutline(cx, cy, scale, outlineColor, outlineStroke)
        "house" -> drawHouseOutline(cx, cy, scale, outlineColor, outlineStroke)
        "fish" -> drawFishOutline(cx, cy, scale, outlineColor, outlineStroke)
        "car" -> drawCarOutline(cx, cy, scale, outlineColor, outlineStroke)
        "tree" -> drawTreeOutline(cx, cy, scale, outlineColor, outlineStroke)
        "rainbow" -> drawRainbowOutline(cx, cy, scale, outlineColor, outlineStroke)
        "rocket" -> drawRocketOutline(cx, cy, scale, outlineColor, outlineStroke)
        "ice_cream" -> drawIceCreamOutline(cx, cy, scale, outlineColor, outlineStroke)
    }
}

// Star outline
private fun DrawScope.drawStarOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        val outerRadius = 120 * scale
        val innerRadius = 50 * scale
        val points = 5

        for (i in 0 until points * 2) {
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val angle = (i * PI / points - PI / 2).toFloat()
            val x = cx + radius * cos(angle)
            val y = cy + radius * sin(angle)
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color, style = stroke)
}

// Heart outline
private fun DrawScope.drawHeartOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    val path = Path().apply {
        val s = 100 * scale
        moveTo(cx, cy + s * 0.8f)
        cubicTo(
            cx - s * 1.5f, cy - s * 0.2f,
            cx - s * 0.8f, cy - s * 1.2f,
            cx, cy - s * 0.4f
        )
        cubicTo(
            cx + s * 0.8f, cy - s * 1.2f,
            cx + s * 1.5f, cy - s * 0.2f,
            cx, cy + s * 0.8f
        )
        close()
    }
    drawPath(path, color, style = stroke)
}

// Sun outline
private fun DrawScope.drawSunOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Center circle
    drawCircle(color, radius = 60 * scale, center = Offset(cx, cy), style = stroke)

    // Rays
    val rayCount = 12
    val innerRadius = 70 * scale
    val outerRadius = 120 * scale

    for (i in 0 until rayCount) {
        val angle = (i * 2 * PI / rayCount).toFloat()
        val x1 = cx + innerRadius * cos(angle)
        val y1 = cy + innerRadius * sin(angle)
        val x2 = cx + outerRadius * cos(angle)
        val y2 = cy + outerRadius * sin(angle)
        drawLine(color, Offset(x1, y1), Offset(x2, y2), strokeWidth = stroke.width)
    }
}

// Flower outline
private fun DrawScope.drawFlowerOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    val petalCount = 5
    val petalRadius = 50 * scale
    val petalDistance = 50 * scale

    // Petals
    for (i in 0 until petalCount) {
        val angle = (i * 2 * PI / petalCount - PI / 2).toFloat()
        val petalCx = cx + petalDistance * cos(angle)
        val petalCy = cy - 20 * scale + petalDistance * sin(angle)
        drawCircle(color, radius = petalRadius, center = Offset(petalCx, petalCy), style = stroke)
    }

    // Center
    drawCircle(color, radius = 30 * scale, center = Offset(cx, cy - 20 * scale), style = stroke)

    // Stem
    drawLine(
        color,
        Offset(cx, cy + 20 * scale),
        Offset(cx, cy + 140 * scale),
        strokeWidth = stroke.width
    )

    // Leaves
    val leafPath1 = Path().apply {
        moveTo(cx, cy + 60 * scale)
        quadraticTo(cx - 40 * scale, cy + 50 * scale, cx - 50 * scale, cy + 80 * scale)
        quadraticTo(cx - 30 * scale, cy + 70 * scale, cx, cy + 80 * scale)
    }
    drawPath(leafPath1, color, style = stroke)

    val leafPath2 = Path().apply {
        moveTo(cx, cy + 90 * scale)
        quadraticTo(cx + 40 * scale, cy + 80 * scale, cx + 50 * scale, cy + 110 * scale)
        quadraticTo(cx + 30 * scale, cy + 100 * scale, cx, cy + 110 * scale)
    }
    drawPath(leafPath2, color, style = stroke)
}

// Butterfly outline
private fun DrawScope.drawButterflyOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Body
    drawOval(
        color,
        topLeft = Offset(cx - 12 * scale, cy - 70 * scale),
        size = androidx.compose.ui.geometry.Size(24 * scale, 140 * scale),
        style = stroke
    )

    // Antennae
    drawLine(color, Offset(cx - 5 * scale, cy - 70 * scale), Offset(cx - 25 * scale, cy - 100 * scale), strokeWidth = stroke.width)
    drawLine(color, Offset(cx + 5 * scale, cy - 70 * scale), Offset(cx + 25 * scale, cy - 100 * scale), strokeWidth = stroke.width)

    // Left wings
    val leftTopWing = Path().apply {
        moveTo(cx - 12 * scale, cy - 30 * scale)
        cubicTo(
            cx - 100 * scale, cy - 100 * scale,
            cx - 120 * scale, cy - 10 * scale,
            cx - 12 * scale, cy + 10 * scale
        )
    }
    drawPath(leftTopWing, color, style = stroke)

    val leftBottomWing = Path().apply {
        moveTo(cx - 12 * scale, cy + 10 * scale)
        cubicTo(
            cx - 90 * scale, cy + 30 * scale,
            cx - 80 * scale, cy + 90 * scale,
            cx - 12 * scale, cy + 50 * scale
        )
    }
    drawPath(leftBottomWing, color, style = stroke)

    // Right wings
    val rightTopWing = Path().apply {
        moveTo(cx + 12 * scale, cy - 30 * scale)
        cubicTo(
            cx + 100 * scale, cy - 100 * scale,
            cx + 120 * scale, cy - 10 * scale,
            cx + 12 * scale, cy + 10 * scale
        )
    }
    drawPath(rightTopWing, color, style = stroke)

    val rightBottomWing = Path().apply {
        moveTo(cx + 12 * scale, cy + 10 * scale)
        cubicTo(
            cx + 90 * scale, cy + 30 * scale,
            cx + 80 * scale, cy + 90 * scale,
            cx + 12 * scale, cy + 50 * scale
        )
    }
    drawPath(rightBottomWing, color, style = stroke)

    // Wing patterns (circles inside wings)
    drawCircle(color, radius = 15 * scale, center = Offset(cx - 60 * scale, cy - 30 * scale), style = stroke)
    drawCircle(color, radius = 15 * scale, center = Offset(cx + 60 * scale, cy - 30 * scale), style = stroke)
    drawCircle(color, radius = 10 * scale, center = Offset(cx - 50 * scale, cy + 35 * scale), style = stroke)
    drawCircle(color, radius = 10 * scale, center = Offset(cx + 50 * scale, cy + 35 * scale), style = stroke)
}

// House outline
private fun DrawScope.drawHouseOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Roof
    val roofPath = Path().apply {
        moveTo(cx, cy - 100 * scale)
        lineTo(cx - 130 * scale, cy - 20 * scale)
        lineTo(cx + 130 * scale, cy - 20 * scale)
        close()
    }
    drawPath(roofPath, color, style = stroke)

    // Wall
    drawRect(
        color,
        topLeft = Offset(cx - 110 * scale, cy - 20 * scale),
        size = androidx.compose.ui.geometry.Size(220 * scale, 130 * scale),
        style = stroke
    )

    // Door
    drawRect(
        color,
        topLeft = Offset(cx - 25 * scale, cy + 30 * scale),
        size = androidx.compose.ui.geometry.Size(50 * scale, 80 * scale),
        style = stroke
    )

    // Door knob
    drawCircle(color, radius = 5 * scale, center = Offset(cx + 15 * scale, cy + 70 * scale), style = stroke)

    // Windows
    drawRect(
        color,
        topLeft = Offset(cx - 90 * scale, cy + 10 * scale),
        size = androidx.compose.ui.geometry.Size(45 * scale, 45 * scale),
        style = stroke
    )
    // Window cross
    drawLine(color, Offset(cx - 67.5f * scale, cy + 10 * scale), Offset(cx - 67.5f * scale, cy + 55 * scale), strokeWidth = stroke.width)
    drawLine(color, Offset(cx - 90 * scale, cy + 32.5f * scale), Offset(cx - 45 * scale, cy + 32.5f * scale), strokeWidth = stroke.width)

    drawRect(
        color,
        topLeft = Offset(cx + 45 * scale, cy + 10 * scale),
        size = androidx.compose.ui.geometry.Size(45 * scale, 45 * scale),
        style = stroke
    )
    // Window cross
    drawLine(color, Offset(cx + 67.5f * scale, cy + 10 * scale), Offset(cx + 67.5f * scale, cy + 55 * scale), strokeWidth = stroke.width)
    drawLine(color, Offset(cx + 45 * scale, cy + 32.5f * scale), Offset(cx + 90 * scale, cy + 32.5f * scale), strokeWidth = stroke.width)

    // Chimney
    drawRect(
        color,
        topLeft = Offset(cx + 50 * scale, cy - 90 * scale),
        size = androidx.compose.ui.geometry.Size(30 * scale, 50 * scale),
        style = stroke
    )
}

// Fish outline
private fun DrawScope.drawFishOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Body
    drawOval(
        color,
        topLeft = Offset(cx - 90 * scale, cy - 50 * scale),
        size = androidx.compose.ui.geometry.Size(150 * scale, 100 * scale),
        style = stroke
    )

    // Tail
    val tailPath = Path().apply {
        moveTo(cx + 50 * scale, cy)
        lineTo(cx + 110 * scale, cy - 50 * scale)
        lineTo(cx + 110 * scale, cy + 50 * scale)
        close()
    }
    drawPath(tailPath, color, style = stroke)

    // Top fin
    val topFinPath = Path().apply {
        moveTo(cx - 20 * scale, cy - 45 * scale)
        lineTo(cx, cy - 90 * scale)
        lineTo(cx + 30 * scale, cy - 45 * scale)
    }
    drawPath(topFinPath, color, style = stroke)

    // Bottom fin
    val bottomFinPath = Path().apply {
        moveTo(cx - 10 * scale, cy + 45 * scale)
        lineTo(cx + 10 * scale, cy + 70 * scale)
        lineTo(cx + 30 * scale, cy + 45 * scale)
    }
    drawPath(bottomFinPath, color, style = stroke)

    // Eye
    drawCircle(color, radius = 12 * scale, center = Offset(cx - 45 * scale, cy - 10 * scale), style = stroke)
    drawCircle(color, radius = 5 * scale, center = Offset(cx - 45 * scale, cy - 10 * scale))

    // Scales pattern
    for (row in 0..2) {
        for (col in 0..3) {
            val scaleX = cx - 30 * scale + col * 25 * scale
            val scaleY = cy - 20 * scale + row * 20 * scale
            drawArc(
                color,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(scaleX - 10 * scale, scaleY - 8 * scale),
                size = androidx.compose.ui.geometry.Size(20 * scale, 16 * scale),
                style = stroke
            )
        }
    }
}

// Car outline
private fun DrawScope.drawCarOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Body
    val bodyPath = Path().apply {
        moveTo(cx - 120 * scale, cy + 40 * scale)
        lineTo(cx - 120 * scale, cy)
        lineTo(cx - 80 * scale, cy)
        lineTo(cx - 60 * scale, cy - 50 * scale)
        lineTo(cx + 60 * scale, cy - 50 * scale)
        lineTo(cx + 80 * scale, cy)
        lineTo(cx + 120 * scale, cy)
        lineTo(cx + 120 * scale, cy + 40 * scale)
        close()
    }
    drawPath(bodyPath, color, style = stroke)

    // Windows
    val leftWindow = Path().apply {
        moveTo(cx - 55 * scale, cy - 5 * scale)
        lineTo(cx - 55 * scale, cy - 45 * scale)
        lineTo(cx - 10 * scale, cy - 45 * scale)
        lineTo(cx - 10 * scale, cy - 5 * scale)
        close()
    }
    drawPath(leftWindow, color, style = stroke)

    val rightWindow = Path().apply {
        moveTo(cx + 10 * scale, cy - 5 * scale)
        lineTo(cx + 10 * scale, cy - 45 * scale)
        lineTo(cx + 55 * scale, cy - 45 * scale)
        lineTo(cx + 55 * scale, cy - 5 * scale)
        close()
    }
    drawPath(rightWindow, color, style = stroke)

    // Wheels
    drawCircle(color, radius = 25 * scale, center = Offset(cx - 70 * scale, cy + 40 * scale), style = stroke)
    drawCircle(color, radius = 12 * scale, center = Offset(cx - 70 * scale, cy + 40 * scale), style = stroke)

    drawCircle(color, radius = 25 * scale, center = Offset(cx + 70 * scale, cy + 40 * scale), style = stroke)
    drawCircle(color, radius = 12 * scale, center = Offset(cx + 70 * scale, cy + 40 * scale), style = stroke)

    // Headlights
    drawCircle(color, radius = 8 * scale, center = Offset(cx - 105 * scale, cy + 15 * scale), style = stroke)
    drawCircle(color, radius = 8 * scale, center = Offset(cx + 105 * scale, cy + 15 * scale), style = stroke)
}

// Tree outline
private fun DrawScope.drawTreeOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Trunk
    drawRect(
        color,
        topLeft = Offset(cx - 25 * scale, cy + 30 * scale),
        size = androidx.compose.ui.geometry.Size(50 * scale, 100 * scale),
        style = stroke
    )

    // Foliage layers
    val layer1 = Path().apply {
        moveTo(cx, cy - 120 * scale)
        lineTo(cx - 60 * scale, cy - 40 * scale)
        lineTo(cx + 60 * scale, cy - 40 * scale)
        close()
    }
    drawPath(layer1, color, style = stroke)

    val layer2 = Path().apply {
        moveTo(cx, cy - 80 * scale)
        lineTo(cx - 80 * scale, cy + 10 * scale)
        lineTo(cx + 80 * scale, cy + 10 * scale)
        close()
    }
    drawPath(layer2, color, style = stroke)

    val layer3 = Path().apply {
        moveTo(cx, cy - 40 * scale)
        lineTo(cx - 100 * scale, cy + 50 * scale)
        lineTo(cx + 100 * scale, cy + 50 * scale)
        close()
    }
    drawPath(layer3, color, style = stroke)
}

// Rainbow outline
private fun DrawScope.drawRainbowOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    val arcCount = 6
    val startRadius = 140 * scale
    val arcWidth = 18 * scale

    for (i in 0 until arcCount) {
        val radius = startRadius - i * arcWidth
        drawArc(
            color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(cx - radius, cy - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = stroke
        )
    }

    // Clouds
    // Left cloud
    drawCircle(color, radius = 25 * scale, center = Offset(cx - 140 * scale, cy + 10 * scale), style = stroke)
    drawCircle(color, radius = 20 * scale, center = Offset(cx - 165 * scale, cy + 15 * scale), style = stroke)
    drawCircle(color, radius = 22 * scale, center = Offset(cx - 115 * scale, cy + 15 * scale), style = stroke)

    // Right cloud
    drawCircle(color, radius = 25 * scale, center = Offset(cx + 140 * scale, cy + 10 * scale), style = stroke)
    drawCircle(color, radius = 20 * scale, center = Offset(cx + 165 * scale, cy + 15 * scale), style = stroke)
    drawCircle(color, radius = 22 * scale, center = Offset(cx + 115 * scale, cy + 15 * scale), style = stroke)
}

// Rocket outline
private fun DrawScope.drawRocketOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Body
    val bodyPath = Path().apply {
        moveTo(cx, cy - 120 * scale)
        quadraticTo(cx + 40 * scale, cy - 80 * scale, cx + 35 * scale, cy + 60 * scale)
        lineTo(cx - 35 * scale, cy + 60 * scale)
        quadraticTo(cx - 40 * scale, cy - 80 * scale, cx, cy - 120 * scale)
    }
    drawPath(bodyPath, color, style = stroke)

    // Window
    drawCircle(color, radius = 20 * scale, center = Offset(cx, cy - 40 * scale), style = stroke)

    // Fins
    val leftFin = Path().apply {
        moveTo(cx - 35 * scale, cy + 30 * scale)
        lineTo(cx - 70 * scale, cy + 90 * scale)
        lineTo(cx - 35 * scale, cy + 60 * scale)
    }
    drawPath(leftFin, color, style = stroke)

    val rightFin = Path().apply {
        moveTo(cx + 35 * scale, cy + 30 * scale)
        lineTo(cx + 70 * scale, cy + 90 * scale)
        lineTo(cx + 35 * scale, cy + 60 * scale)
    }
    drawPath(rightFin, color, style = stroke)

    // Flame
    val flamePath = Path().apply {
        moveTo(cx - 25 * scale, cy + 60 * scale)
        quadraticTo(cx - 15 * scale, cy + 100 * scale, cx, cy + 130 * scale)
        quadraticTo(cx + 15 * scale, cy + 100 * scale, cx + 25 * scale, cy + 60 * scale)
    }
    drawPath(flamePath, color, style = stroke)

    // Inner flame
    val innerFlame = Path().apply {
        moveTo(cx - 12 * scale, cy + 60 * scale)
        quadraticTo(cx, cy + 90 * scale, cx + 12 * scale, cy + 60 * scale)
    }
    drawPath(innerFlame, color, style = stroke)
}

// Ice Cream outline
private fun DrawScope.drawIceCreamOutline(cx: Float, cy: Float, scale: Float, color: Color, stroke: Stroke) {
    // Cone
    val conePath = Path().apply {
        moveTo(cx - 50 * scale, cy + 20 * scale)
        lineTo(cx, cy + 140 * scale)
        lineTo(cx + 50 * scale, cy + 20 * scale)
    }
    drawPath(conePath, color, style = stroke)

    // Cone pattern
    for (i in 0..4) {
        val y = cy + 30 * scale + i * 20 * scale
        val halfWidth = 45 * scale - i * 8 * scale
        drawLine(color, Offset(cx - halfWidth, y), Offset(cx + halfWidth, y), strokeWidth = stroke.width / 2)
    }

    // Diagonal lines on cone
    for (i in -3..3) {
        val startX = cx + i * 15 * scale
        drawLine(
            color,
            Offset(startX - 20 * scale, cy + 20 * scale),
            Offset(startX + 10 * scale, cy + 100 * scale),
            strokeWidth = stroke.width / 2
        )
    }

    // Scoops
    drawCircle(color, radius = 45 * scale, center = Offset(cx, cy - 60 * scale), style = stroke)
    drawCircle(color, radius = 50 * scale, center = Offset(cx, cy + 5 * scale), style = stroke)

    // Cherry on top
    drawCircle(color, radius = 12 * scale, center = Offset(cx, cy - 115 * scale), style = stroke)
    // Cherry stem
    val stemPath = Path().apply {
        moveTo(cx, cy - 127 * scale)
        quadraticTo(cx + 15 * scale, cy - 140 * scale, cx + 10 * scale, cy - 150 * scale)
    }
    drawPath(stemPath, color, style = stroke)

    // Drips
    val dripPath1 = Path().apply {
        moveTo(cx - 30 * scale, cy + 5 * scale)
        quadraticTo(cx - 35 * scale, cy + 25 * scale, cx - 25 * scale, cy + 15 * scale)
    }
    drawPath(dripPath1, color, style = stroke)

    val dripPath2 = Path().apply {
        moveTo(cx + 25 * scale, cy - 10 * scale)
        quadraticTo(cx + 35 * scale, cy + 10 * scale, cx + 20 * scale, cy + 5 * scale)
    }
    drawPath(dripPath2, color, style = stroke)
}
