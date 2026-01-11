package com.example.alphabettracer.data

import androidx.compose.ui.graphics.Color

// Stick colors available in the tray
val stickColors = listOf(
    Color(0xFFE53935), // Red
    Color(0xFF1E88E5), // Blue
    Color(0xFF43A047), // Green
    Color(0xFFFFB300), // Yellow/Orange
    Color(0xFF8E24AA), // Purple
    Color(0xFFFF7043)  // Coral/Orange
)

/**
 * 7-Segment Display Layout:
 *
 *    ═══ A ═══
 *   ║         ║
 *   F         B
 *   ║         ║
 *    ═══ G ═══
 *   ║         ║
 *   E         C
 *   ║         ║
 *    ═══ D ═══
 *
 * All horizontal segments (A, G, D) are the same length
 * All vertical segments (B, C, E, F) are the same length
 */

// The 7 segment positions
enum class SegmentPosition {
    A,  // Top horizontal
    B,  // Top-right vertical
    C,  // Bottom-right vertical
    D,  // Bottom horizontal
    E,  // Bottom-left vertical
    F,  // Top-left vertical
    G   // Middle horizontal
}

// Stick orientation - either horizontal or vertical (same size within each type)
enum class StickOrientation {
    HORIZONTAL,
    VERTICAL
}

// A segment slot represents a position where a stick can be placed
data class SegmentSlot(
    val position: SegmentPosition,
    val orientation: StickOrientation,
    val centerX: Float,  // Center position normalized 0-1
    val centerY: Float,
    val isHorizontal: Boolean = orientation == StickOrientation.HORIZONTAL
)

// Define the 7 segment slots with their positions
// Using a clean 7-segment layout where all sticks are uniform
object SevenSegmentLayout {
    // Layout dimensions (normalized 0-1)
    private const val LEFT = 0.25f
    private const val RIGHT = 0.75f
    private const val TOP = 0.1f
    private const val MIDDLE = 0.5f
    private const val BOTTOM = 0.9f
    private const val CENTER_X = 0.5f

    // Stick length (normalized)
    const val HORIZONTAL_LENGTH = 0.5f  // Length for horizontal sticks
    const val VERTICAL_LENGTH = 0.4f    // Length for vertical sticks (half height)

    val slots = listOf(
        // A - Top horizontal
        SegmentSlot(SegmentPosition.A, StickOrientation.HORIZONTAL, CENTER_X, TOP),
        // B - Top-right vertical
        SegmentSlot(SegmentPosition.B, StickOrientation.VERTICAL, RIGHT, (TOP + MIDDLE) / 2),
        // C - Bottom-right vertical
        SegmentSlot(SegmentPosition.C, StickOrientation.VERTICAL, RIGHT, (MIDDLE + BOTTOM) / 2),
        // D - Bottom horizontal
        SegmentSlot(SegmentPosition.D, StickOrientation.HORIZONTAL, CENTER_X, BOTTOM),
        // E - Bottom-left vertical
        SegmentSlot(SegmentPosition.E, StickOrientation.VERTICAL, LEFT, (MIDDLE + BOTTOM) / 2),
        // F - Top-left vertical
        SegmentSlot(SegmentPosition.F, StickOrientation.VERTICAL, LEFT, (TOP + MIDDLE) / 2),
        // G - Middle horizontal
        SegmentSlot(SegmentPosition.G, StickOrientation.HORIZONTAL, CENTER_X, MIDDLE)
    )

    fun getSlot(position: SegmentPosition): SegmentSlot {
        return slots.first { it.position == position }
    }
}

// Which segments are ON for each digit (0-9)
object DigitSegments {
    private val digitPatterns = mapOf(
        0 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.C, SegmentPosition.D, SegmentPosition.E, SegmentPosition.F),
        1 to setOf(SegmentPosition.B, SegmentPosition.C),
        2 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.G, SegmentPosition.E, SegmentPosition.D),
        3 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.G, SegmentPosition.C, SegmentPosition.D),
        4 to setOf(SegmentPosition.F, SegmentPosition.G, SegmentPosition.B, SegmentPosition.C),
        5 to setOf(SegmentPosition.A, SegmentPosition.F, SegmentPosition.G, SegmentPosition.C, SegmentPosition.D),
        6 to setOf(SegmentPosition.A, SegmentPosition.F, SegmentPosition.G, SegmentPosition.E, SegmentPosition.D, SegmentPosition.C),
        7 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.C),
        8 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.C, SegmentPosition.D, SegmentPosition.E, SegmentPosition.F, SegmentPosition.G),
        9 to setOf(SegmentPosition.A, SegmentPosition.B, SegmentPosition.C, SegmentPosition.D, SegmentPosition.F, SegmentPosition.G)
    )

    fun getSegmentsForDigit(digit: Int): Set<SegmentPosition> {
        return digitPatterns[digit] ?: emptySet()
    }

    fun getStickCountForDigit(digit: Int): Int {
        return getSegmentsForDigit(digit).size
    }

    // Recognize what digit is formed by the given segments
    // Returns null if no valid digit matches
    fun recognizeDigit(activeSegments: Set<SegmentPosition>): Int? {
        for ((digit, pattern) in digitPatterns) {
            if (activeSegments == pattern) {
                return digit
            }
        }
        return null
    }
}

// Legacy support - maps old StickSegment to new format
data class StickSegment(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val id: Int = 0
)

// Convert 7-segment patterns to old StickSegment format for compatibility
object NumberStickPatterns {

    private fun segmentToStickSegment(slot: SegmentSlot, id: Int): StickSegment {
        val halfLength = if (slot.isHorizontal) {
            SevenSegmentLayout.HORIZONTAL_LENGTH / 2
        } else {
            SevenSegmentLayout.VERTICAL_LENGTH / 2
        }

        return if (slot.isHorizontal) {
            StickSegment(
                startX = slot.centerX - halfLength,
                startY = slot.centerY,
                endX = slot.centerX + halfLength,
                endY = slot.centerY,
                id = id
            )
        } else {
            StickSegment(
                startX = slot.centerX,
                startY = slot.centerY - halfLength,
                endX = slot.centerX,
                endY = slot.centerY + halfLength,
                id = id
            )
        }
    }

    fun getPatternForNumber(number: Int): List<StickSegment> {
        val segments = DigitSegments.getSegmentsForDigit(number)
        return segments.mapIndexed { index, position ->
            val slot = SevenSegmentLayout.getSlot(position)
            segmentToStickSegment(slot, index)
        }
    }
}

// Represents a challenge level
data class StickBuilderChallenge(
    val id: Int,
    val type: ChallengeType,
    val targetNumber: Int? = null,           // For single number challenges
    val equation: String? = null,             // For equation challenges like "2 + 3 = ?"
    val answer: Int? = null,                  // Answer for equations
    val displayText: String                   // What to show to user
)

enum class ChallengeType {
    BUILD_NUMBER,      // Simply build the shown number
    EQUATION_RESULT    // Build the result of an equation
}

// Game levels/challenges
object StickBuilderLevels {
    val allChallenges = listOf(
        // Level 1-10: Build single numbers 0-9
        StickBuilderChallenge(1, ChallengeType.BUILD_NUMBER, targetNumber = 1, displayText = "Build: 1"),
        StickBuilderChallenge(2, ChallengeType.BUILD_NUMBER, targetNumber = 4, displayText = "Build: 4"),
        StickBuilderChallenge(3, ChallengeType.BUILD_NUMBER, targetNumber = 7, displayText = "Build: 7"),
        StickBuilderChallenge(4, ChallengeType.BUILD_NUMBER, targetNumber = 0, displayText = "Build: 0"),
        StickBuilderChallenge(5, ChallengeType.BUILD_NUMBER, targetNumber = 2, displayText = "Build: 2"),
        StickBuilderChallenge(6, ChallengeType.BUILD_NUMBER, targetNumber = 3, displayText = "Build: 3"),
        StickBuilderChallenge(7, ChallengeType.BUILD_NUMBER, targetNumber = 5, displayText = "Build: 5"),
        StickBuilderChallenge(8, ChallengeType.BUILD_NUMBER, targetNumber = 6, displayText = "Build: 6"),
        StickBuilderChallenge(9, ChallengeType.BUILD_NUMBER, targetNumber = 8, displayText = "Build: 8"),
        StickBuilderChallenge(10, ChallengeType.BUILD_NUMBER, targetNumber = 9, displayText = "Build: 9"),

        // Level 11-20: Simple addition equations
        StickBuilderChallenge(11, ChallengeType.EQUATION_RESULT, equation = "1 + 1 = ?", answer = 2, displayText = "1 + 1 = ?"),
        StickBuilderChallenge(12, ChallengeType.EQUATION_RESULT, equation = "2 + 1 = ?", answer = 3, displayText = "2 + 1 = ?"),
        StickBuilderChallenge(13, ChallengeType.EQUATION_RESULT, equation = "2 + 2 = ?", answer = 4, displayText = "2 + 2 = ?"),
        StickBuilderChallenge(14, ChallengeType.EQUATION_RESULT, equation = "3 + 2 = ?", answer = 5, displayText = "3 + 2 = ?"),
        StickBuilderChallenge(15, ChallengeType.EQUATION_RESULT, equation = "4 + 2 = ?", answer = 6, displayText = "4 + 2 = ?"),
        StickBuilderChallenge(16, ChallengeType.EQUATION_RESULT, equation = "5 + 2 = ?", answer = 7, displayText = "5 + 2 = ?"),
        StickBuilderChallenge(17, ChallengeType.EQUATION_RESULT, equation = "4 + 4 = ?", answer = 8, displayText = "4 + 4 = ?"),
        StickBuilderChallenge(18, ChallengeType.EQUATION_RESULT, equation = "5 + 4 = ?", answer = 9, displayText = "5 + 4 = ?"),
        StickBuilderChallenge(19, ChallengeType.EQUATION_RESULT, equation = "3 + 3 = ?", answer = 6, displayText = "3 + 3 = ?"),
        StickBuilderChallenge(20, ChallengeType.EQUATION_RESULT, equation = "1 + 0 = ?", answer = 1, displayText = "1 + 0 = ?"),

        // Level 21-30: Simple subtraction equations
        StickBuilderChallenge(21, ChallengeType.EQUATION_RESULT, equation = "3 - 1 = ?", answer = 2, displayText = "3 - 1 = ?"),
        StickBuilderChallenge(22, ChallengeType.EQUATION_RESULT, equation = "5 - 2 = ?", answer = 3, displayText = "5 - 2 = ?"),
        StickBuilderChallenge(23, ChallengeType.EQUATION_RESULT, equation = "7 - 3 = ?", answer = 4, displayText = "7 - 3 = ?"),
        StickBuilderChallenge(24, ChallengeType.EQUATION_RESULT, equation = "9 - 4 = ?", answer = 5, displayText = "9 - 4 = ?"),
        StickBuilderChallenge(25, ChallengeType.EQUATION_RESULT, equation = "8 - 2 = ?", answer = 6, displayText = "8 - 2 = ?"),
        StickBuilderChallenge(26, ChallengeType.EQUATION_RESULT, equation = "9 - 2 = ?", answer = 7, displayText = "9 - 2 = ?"),
        StickBuilderChallenge(27, ChallengeType.EQUATION_RESULT, equation = "9 - 1 = ?", answer = 8, displayText = "9 - 1 = ?"),
        StickBuilderChallenge(28, ChallengeType.EQUATION_RESULT, equation = "9 - 0 = ?", answer = 9, displayText = "9 - 0 = ?"),
        StickBuilderChallenge(29, ChallengeType.EQUATION_RESULT, equation = "5 - 5 = ?", answer = 0, displayText = "5 - 5 = ?"),
        StickBuilderChallenge(30, ChallengeType.EQUATION_RESULT, equation = "4 - 3 = ?", answer = 1, displayText = "4 - 3 = ?")
    )

    fun getChallengeById(id: Int): StickBuilderChallenge? {
        return allChallenges.find { it.id == id }
    }

    fun getTargetNumber(challenge: StickBuilderChallenge): Int {
        return when (challenge.type) {
            ChallengeType.BUILD_NUMBER -> challenge.targetNumber ?: 0
            ChallengeType.EQUATION_RESULT -> challenge.answer ?: 0
        }
    }
}
