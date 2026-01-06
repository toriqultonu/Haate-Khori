package com.example.alphabettracer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.sharp.AddCircle
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.ui.theme.AlphabetTracerTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
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

// Data classes
data class AlphabetData(val letter: Char, val word: String, val sentence: String)
data class DrawStroke(val points: List<Offset>, val color: Color, val width: Float)
enum class MatchResult { NONE, POOR, GOOD, EXCELLENT }
enum class ScreenState { LETTER_GRID, TRACING }

// Storage helper for persisting letter results
object LetterStorage {
    private const val PREFS_NAME = "haate_khori_prefs"
    private const val KEY_PREFIX = "letter_result_"

    fun saveLetterResult(context: Context, letterIndex: Int, result: MatchResult) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentResult = getLetterResult(context, letterIndex)
        // Only save if new result is better than existing
        if (result.ordinal > currentResult.ordinal) {
            prefs.edit().putInt("$KEY_PREFIX$letterIndex", result.ordinal).apply()
        }
    }

    fun getLetterResult(context: Context, letterIndex: Int): MatchResult {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ordinal = prefs.getInt("$KEY_PREFIX$letterIndex", MatchResult.NONE.ordinal)
        return MatchResult.entries[ordinal]
    }

    fun getAllResults(context: Context, count: Int): Map<Int, MatchResult> {
        return (0 until count).associateWith { getLetterResult(context, it) }
    }

    fun getTotalStars(context: Context, count: Int): Int {
        return (0 until count).count { getLetterResult(context, it) == MatchResult.EXCELLENT }
    }

    fun clearAllResults(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}

// Complete alphabet A-Z
val alphabetList = listOf(
    AlphabetData('A', "Apple", "A is for Apple"),
    AlphabetData('B', "Ball", "B is for Ball"),
    AlphabetData('C', "Cat", "C is for Cat"),
    AlphabetData('D', "Dog", "D is for Dog"),
    AlphabetData('E', "Elephant", "E is for Elephant"),
    AlphabetData('F', "Fish", "F is for Fish"),
    AlphabetData('G', "Giraffe", "G is for Giraffe"),
    AlphabetData('H', "House", "H is for House"),
    AlphabetData('I', "Ice cream", "I is for Ice cream"),
    AlphabetData('J', "Jelly", "J is for Jelly"),
    AlphabetData('K', "Kite", "K is for Kite"),
    AlphabetData('L', "Lion", "L is for Lion"),
    AlphabetData('M', "Monkey", "M is for Monkey"),
    AlphabetData('N', "Nest", "N is for Nest"),
    AlphabetData('O', "Orange", "O is for Orange"),
    AlphabetData('P', "Penguin", "P is for Penguin"),
    AlphabetData('Q', "Queen", "Q is for Queen"),
    AlphabetData('R', "Rainbow", "R is for Rainbow"),
    AlphabetData('S', "Sun", "S is for Sun"),
    AlphabetData('T', "Tiger", "T is for Tiger"),
    AlphabetData('U', "Umbrella", "U is for Umbrella"),
    AlphabetData('V', "Violin", "V is for Violin"),
    AlphabetData('W', "Whale", "W is for Whale"),
    AlphabetData('X', "Xylophone", "X is for Xylophone"),
    AlphabetData('Y', "Yak", "Y is for Yak"),
    AlphabetData('Z', "Zebra", "Z is for Zebra")
)

// Generate detailed letter paths with many interpolated points for better detection
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTracingApp() {
    val context = LocalContext.current
    var screenState by remember { mutableStateOf(ScreenState.LETTER_GRID) }
    var currentIndex by remember { mutableStateOf(0) }
    var userStreak by remember { mutableStateOf(0) }

    // Load saved results from storage
    var letterResults by remember {
        mutableStateOf(LetterStorage.getAllResults(context, alphabetList.size))
    }
    var totalStars by remember {
        mutableStateOf(LetterStorage.getTotalStars(context, alphabetList.size))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Haate Khori",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(12.dp))
                        // Stars counter
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 16.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("$totalStars", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (screenState == ScreenState.TRACING) {
                        IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to grid")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            when (screenState) {
                ScreenState.LETTER_GRID -> {
                    LetterGridScreen(
                        letterResults = letterResults,
                        onLetterSelected = { index ->
                            currentIndex = index
                            screenState = ScreenState.TRACING
                        }
                    )
                }
                ScreenState.TRACING -> {
                    TracingScreen(
                        currentIndex = currentIndex,
                        userStreak = userStreak,
                        onStreakUpdate = { newStreak -> userStreak = newStreak },
                        onResultSaved = { index, result ->
                            // Save to persistent storage
                            LetterStorage.saveLetterResult(context, index, result)
                            // Update local state
                            letterResults = LetterStorage.getAllResults(context, alphabetList.size)
                            totalStars = LetterStorage.getTotalStars(context, alphabetList.size)
                        },
                        onNavigate = { newIndex ->
                            currentIndex = newIndex
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LetterGridScreen(
    letterResults: Map<Int, MatchResult>,
    onLetterSelected: (Int) -> Unit
) {
    // Count letters with at least GOOD result for progress
    val completedCount = letterResults.count { it.value.ordinal >= MatchResult.GOOD.ordinal }
    val excellentCount = letterResults.count { it.value == MatchResult.EXCELLENT }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        val progress = excellentCount.toFloat() / alphabetList.size
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Your Progress",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 14.sp)
                        Text(
                            " $excellentCount/${alphabetList.size}",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )
            }
        }

        Text(
            "Choose a letter to practice:",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Letter grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(alphabetList) { index, item ->
                val result = letterResults[index] ?: MatchResult.NONE
                LetterGridItem(
                    letter = item.letter,
                    result = result,
                    onClick = { onLetterSelected(index) }
                )
            }
        }
    }
}

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

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
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
                    Text(icon, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun TracingScreen(
    currentIndex: Int,
    userStreak: Int,
    onStreakUpdate: (Int) -> Unit,
    onResultSaved: (Int, MatchResult) -> Unit,
    onNavigate: (Int) -> Unit
) {
    val context = LocalContext.current
    val currentLetter = alphabetList[currentIndex]
    var showCongratsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Letter info card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Big letter display
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = CircleShape,
                    color = Color(0xFF6200EE).copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = currentLetter.letter.toString(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE)
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = currentLetter.word,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = currentLetter.sentence,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Streak indicator
        AnimatedVisibility(
            visible = userStreak > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = Color(0xFFFF9800).copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Streak: $userStreak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }

        // Tracing canvas
        TracingCanvas(
            letter = currentLetter.letter,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onCheckResult = { result ->
                // Save result (only saves if better than existing)
                if (result != MatchResult.NONE) {
                    onResultSaved(currentIndex, result)
                }

                when (result) {
                    MatchResult.EXCELLENT -> {
                        val newStreak = userStreak + 1
                        onStreakUpdate(newStreak)
                        if (newStreak > 0 && newStreak % 5 == 0) {
                            showCongratsDialog = true
                        }
                        Toast.makeText(context, "Excellent! ⭐", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.GOOD -> {
                        onStreakUpdate(0) // Reset streak on non-excellent
                        Toast.makeText(context, "Good job! Keep practicing!", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.POOR -> {
                        onStreakUpdate(0) // Reset streak
                        Toast.makeText(context, "Keep trying! Trace more of the letter.", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.NONE -> {
                        // Do nothing
                    }
                }
            }
        )

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) {
                        onNavigate(currentIndex - 1)
                    }
                },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                Spacer(Modifier.width(8.dp))
                Text("Previous")
            }

            Button(
                onClick = {
                    if (currentIndex < alphabetList.size - 1) {
                        onNavigate(currentIndex + 1)
                    }
                },
                enabled = currentIndex < alphabetList.size - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }

    // Congrats dialog
    if (showCongratsDialog) {
        AlertDialog(
            onDismissRequest = { showCongratsDialog = false },
            title = {
                Text("🎉 Amazing!", textAlign = TextAlign.Center)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "You got a $userStreak letter streak!",
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("🏆", fontSize = 48.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCongratsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Keep Going!")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

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

fun Offset.distanceTo(other: Offset): Float {
    return sqrt((this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y))
}
