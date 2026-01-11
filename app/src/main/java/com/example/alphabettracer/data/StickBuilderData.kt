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

// Number patterns using stick segments
// Each number is composed of multiple stick segments
// Coordinates are normalized (0-1 range) for easy scaling
object NumberStickPatterns {

    // Number 0 - Rectangle shape
    val zero = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.7f, 0.9f, 1),   // Right vertical
        StickSegment(0.7f, 0.9f, 0.3f, 0.9f, 2),   // Bottom horizontal
        StickSegment(0.3f, 0.9f, 0.3f, 0.1f, 3)    // Left vertical
    )

    // Number 1 - Single vertical stick
    val one = listOf(
        StickSegment(0.5f, 0.1f, 0.5f, 0.9f, 0)    // Center vertical
    )

    // Number 2 - Angular shape
    val two = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.7f, 0.5f, 1),   // Top-right vertical
        StickSegment(0.7f, 0.5f, 0.3f, 0.5f, 2),   // Middle horizontal
        StickSegment(0.3f, 0.5f, 0.3f, 0.9f, 3),   // Bottom-left vertical
        StickSegment(0.3f, 0.9f, 0.7f, 0.9f, 4)    // Bottom horizontal
    )

    // Number 3 - Three horizontal sticks with right verticals
    val three = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.7f, 0.5f, 1),   // Top vertical
        StickSegment(0.4f, 0.5f, 0.7f, 0.5f, 2),   // Middle horizontal
        StickSegment(0.7f, 0.5f, 0.7f, 0.9f, 3),   // Bottom vertical
        StickSegment(0.3f, 0.9f, 0.7f, 0.9f, 4)    // Bottom horizontal
    )

    // Number 4 - Like in the demo image
    val four = listOf(
        StickSegment(0.3f, 0.1f, 0.3f, 0.5f, 0),   // Top-left vertical
        StickSegment(0.3f, 0.5f, 0.7f, 0.5f, 1),   // Middle horizontal
        StickSegment(0.6f, 0.1f, 0.6f, 0.9f, 2)    // Right vertical (full height)
    )

    // Number 5 - Angular S-shape
    val five = listOf(
        StickSegment(0.7f, 0.1f, 0.3f, 0.1f, 0),   // Top horizontal (right to left)
        StickSegment(0.3f, 0.1f, 0.3f, 0.5f, 1),   // Top-left vertical
        StickSegment(0.3f, 0.5f, 0.7f, 0.5f, 2),   // Middle horizontal
        StickSegment(0.7f, 0.5f, 0.7f, 0.9f, 3),   // Bottom-right vertical
        StickSegment(0.7f, 0.9f, 0.3f, 0.9f, 4)    // Bottom horizontal
    )

    // Number 6 -
    val six = listOf(
        StickSegment(0.7f, 0.1f, 0.3f, 0.1f, 0),   // Top horizontal
        StickSegment(0.3f, 0.1f, 0.3f, 0.9f, 1),   // Left vertical (full)
        StickSegment(0.3f, 0.5f, 0.7f, 0.5f, 2),   // Middle horizontal
        StickSegment(0.7f, 0.5f, 0.7f, 0.9f, 3),   // Bottom-right vertical
        StickSegment(0.3f, 0.9f, 0.7f, 0.9f, 4)    // Bottom horizontal
    )

    // Number 7 - Simple angle
    val seven = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.5f, 0.9f, 1)    // Diagonal
    )

    // Number 8 - Two stacked rectangles
    val eight = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.7f, 0.5f, 1),   // Top-right vertical
        StickSegment(0.3f, 0.1f, 0.3f, 0.5f, 2),   // Top-left vertical
        StickSegment(0.3f, 0.5f, 0.7f, 0.5f, 3),   // Middle horizontal
        StickSegment(0.7f, 0.5f, 0.7f, 0.9f, 4),   // Bottom-right vertical
        StickSegment(0.3f, 0.5f, 0.3f, 0.9f, 5),   // Bottom-left vertical
        StickSegment(0.3f, 0.9f, 0.7f, 0.9f, 6)    // Bottom horizontal
    )

    // Number 9 -
    val nine = listOf(
        StickSegment(0.3f, 0.1f, 0.7f, 0.1f, 0),   // Top horizontal
        StickSegment(0.7f, 0.1f, 0.7f, 0.9f, 1),   // Right vertical (full)
        StickSegment(0.3f, 0.1f, 0.3f, 0.5f, 2),   // Top-left vertical
        StickSegment(0.3f, 0.5f, 0.7f, 0.5f, 3),   // Middle horizontal
        StickSegment(0.3f, 0.9f, 0.7f, 0.9f, 4)    // Bottom horizontal
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
