package com.example.alphabettracer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.geometry.Offset
import com.example.alphabettracer.model.DrawStroke
import com.example.alphabettracer.model.MatchResult
import com.example.alphabettracer.ui.screens.AlphabetTracingApp
import com.example.alphabettracer.ui.theme.AlphabetTracerTheme
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlphabetTracerTheme {
                AlphabetTracingApp()
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
            arcPoints(centerX, centerY, 80 * scale, -60f, 60f, 30).reversed() +
            arcPoints(centerX, centerY, 80 * scale, 60f, 300f, 30).reversed()
        }
        'D' -> {
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
            val arc = arcPoints(centerX, centerY, 80 * scale, -60f, 240f, 35)
            val bar = interpolate(
                Offset(centerX + 80 * scale * cos(Math.toRadians(-60.0)).toFloat(),
                       centerY + 80 * scale * sin(Math.toRadians(-60.0)).toFloat()),
                Offset(centerX, centerY),
                10
            )
            arc + bar
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
            val topCurve = arcPoints(centerX, centerY - 40 * scale, 50 * scale, -30f, 210f, 20)
            val bottomCurve = arcPoints(centerX, centerY + 40 * scale, 50 * scale, 150f, 390f, 20)
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
            val curve = arcPoints(centerX, centerY + 30 * scale, 50 * scale, 180f, 360f, 20)
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

// Drawing detection algorithm - balanced for accuracy while being kid-friendly
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
