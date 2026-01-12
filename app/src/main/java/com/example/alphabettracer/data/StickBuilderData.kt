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

// Represents a single stick segment for building numbers
// Coordinates are in a normalized 0-1 range (will be scaled to actual size)
data class StickSegment(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val id: Int = 0
)

/**
 * 7-Segment Display Layout:
 *
 *     ═══ A ═══
 *    ║         ║
 *    F         B
 *    ║         ║
 *     ═══ G ═══
 *    ║         ║
 *    E         C
 *    ║         ║
 *     ═══ D ═══
 *
 * Segments:
 * A = Top horizontal
 * B = Top-right vertical
 * C = Bottom-right vertical
 * D = Bottom horizontal
 * E = Bottom-left vertical
 * F = Top-left vertical
 * G = Middle horizontal
 */
object NumberStickPatterns {

    // Fixed positions for 7-segment display
    private const val LEFT = 0.25f
    private const val RIGHT = 0.75f
    private const val TOP = 0.08f
    private const val MIDDLE = 0.48f
    private const val BOTTOM = 0.88f

    // Segment A - Top horizontal
    private fun segA(id: Int) = StickSegment(LEFT, TOP, RIGHT, TOP, id)

    // Segment B - Top-right vertical
    private fun segB(id: Int) = StickSegment(RIGHT, TOP, RIGHT, MIDDLE, id)

    // Segment C - Bottom-right vertical
    private fun segC(id: Int) = StickSegment(RIGHT, MIDDLE, RIGHT, BOTTOM, id)

    // Segment D - Bottom horizontal
    private fun segD(id: Int) = StickSegment(LEFT, BOTTOM, RIGHT, BOTTOM, id)

    // Segment E - Bottom-left vertical
    private fun segE(id: Int) = StickSegment(LEFT, MIDDLE, LEFT, BOTTOM, id)

    // Segment F - Top-left vertical
    private fun segF(id: Int) = StickSegment(LEFT, TOP, LEFT, MIDDLE, id)

    // Segment G - Middle horizontal
    private fun segG(id: Int) = StickSegment(LEFT, MIDDLE, RIGHT, MIDDLE, id)

    // Number 0: A, B, C, D, E, F (all except G)
    val zero = listOf(
        segA(0), segB(1), segC(2), segD(3), segE(4), segF(5)
    )

    // Number 1: B, C (right side only)
    val one = listOf(
        segB(0), segC(1)
    )

    // Number 2: A, B, G, E, D
    val two = listOf(
        segA(0), segB(1), segG(2), segE(3), segD(4)
    )

    // Number 3: A, B, G, C, D
    val three = listOf(
        segA(0), segB(1), segG(2), segC(3), segD(4)
    )

    // Number 4: F, G, B, C
    val four = listOf(
        segF(0), segG(1), segB(2), segC(3)
    )

    // Number 5: A, F, G, C, D
    val five = listOf(
        segA(0), segF(1), segG(2), segC(3), segD(4)
    )

    // Number 6: A, F, G, E, D, C
    val six = listOf(
        segA(0), segF(1), segG(2), segE(3), segD(4), segC(5)
    )

    // Number 7: A, B, C
    val seven = listOf(
        segA(0), segB(1), segC(2)
    )

    // Number 8: All segments A, B, C, D, E, F, G
    val eight = listOf(
        segA(0), segB(1), segC(2), segD(3), segE(4), segF(5), segG(6)
    )

    // Number 9: A, B, C, D, F, G
    val nine = listOf(
        segA(0), segB(1), segC(2), segD(3), segF(4), segG(5)
    )

    fun getPatternForNumber(number: Int): List<StickSegment> {
        return when (number) {
            0 -> zero
            1 -> one
            2 -> two
            3 -> three
            4 -> four
            5 -> five
            6 -> six
            7 -> seven
            8 -> eight
            9 -> nine
            else -> one
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
