package com.example.alphabettracer.content

/**
 * Stick Builder game content data.
 * Contains level definitions for the 7-segment display game.
 * Can be replaced with backend data in the future.
 */
object StickBuilderContent {

    /**
     * 7-segment display segment mapping:
     *     0
     *    ---
     * 1 |   | 2
     *    ---  <- 3
     * 4 |   | 5
     *    ---
     *     6
     */
    object SegmentPatterns {
        val DIGIT_0 = listOf(0, 1, 2, 4, 5, 6)
        val DIGIT_1 = listOf(2, 5)
        val DIGIT_2 = listOf(0, 2, 3, 4, 6)
        val DIGIT_3 = listOf(0, 2, 3, 5, 6)
        val DIGIT_4 = listOf(1, 2, 3, 5)
        val DIGIT_5 = listOf(0, 1, 3, 5, 6)
        val DIGIT_6 = listOf(0, 1, 3, 4, 5, 6)
        val DIGIT_7 = listOf(0, 2, 5)
        val DIGIT_8 = listOf(0, 1, 2, 3, 4, 5, 6)
        val DIGIT_9 = listOf(0, 1, 2, 3, 5, 6)

        fun getPatternForDigit(digit: Int): List<Int> {
            return when (digit) {
                0 -> DIGIT_0
                1 -> DIGIT_1
                2 -> DIGIT_2
                3 -> DIGIT_3
                4 -> DIGIT_4
                5 -> DIGIT_5
                6 -> DIGIT_6
                7 -> DIGIT_7
                8 -> DIGIT_8
                9 -> DIGIT_9
                else -> emptyList()
            }
        }
    }

    val levels: List<StickBuilderLevelContent> = listOf(
        // Level 1-10: Build single digits
        StickBuilderLevelContent(id = 1, equation = "Build: 0", answer = "0", segmentPattern = SegmentPatterns.DIGIT_0, difficulty = 1),
        StickBuilderLevelContent(id = 2, equation = "Build: 1", answer = "1", segmentPattern = SegmentPatterns.DIGIT_1, difficulty = 1),
        StickBuilderLevelContent(id = 3, equation = "Build: 2", answer = "2", segmentPattern = SegmentPatterns.DIGIT_2, difficulty = 1),
        StickBuilderLevelContent(id = 4, equation = "Build: 3", answer = "3", segmentPattern = SegmentPatterns.DIGIT_3, difficulty = 1),
        StickBuilderLevelContent(id = 5, equation = "Build: 4", answer = "4", segmentPattern = SegmentPatterns.DIGIT_4, difficulty = 1),
        StickBuilderLevelContent(id = 6, equation = "Build: 5", answer = "5", segmentPattern = SegmentPatterns.DIGIT_5, difficulty = 1),
        StickBuilderLevelContent(id = 7, equation = "Build: 6", answer = "6", segmentPattern = SegmentPatterns.DIGIT_6, difficulty = 1),
        StickBuilderLevelContent(id = 8, equation = "Build: 7", answer = "7", segmentPattern = SegmentPatterns.DIGIT_7, difficulty = 1),
        StickBuilderLevelContent(id = 9, equation = "Build: 8", answer = "8", segmentPattern = SegmentPatterns.DIGIT_8, difficulty = 1),
        StickBuilderLevelContent(id = 10, equation = "Build: 9", answer = "9", segmentPattern = SegmentPatterns.DIGIT_9, difficulty = 1),

        // Level 11-20: Addition problems
        StickBuilderLevelContent(id = 11, equation = "1 + 1 = ?", answer = "2", segmentPattern = SegmentPatterns.DIGIT_2, difficulty = 2),
        StickBuilderLevelContent(id = 12, equation = "2 + 1 = ?", answer = "3", segmentPattern = SegmentPatterns.DIGIT_3, difficulty = 2),
        StickBuilderLevelContent(id = 13, equation = "2 + 2 = ?", answer = "4", segmentPattern = SegmentPatterns.DIGIT_4, difficulty = 2),
        StickBuilderLevelContent(id = 14, equation = "3 + 2 = ?", answer = "5", segmentPattern = SegmentPatterns.DIGIT_5, difficulty = 2),
        StickBuilderLevelContent(id = 15, equation = "4 + 2 = ?", answer = "6", segmentPattern = SegmentPatterns.DIGIT_6, difficulty = 2),
        StickBuilderLevelContent(id = 16, equation = "3 + 4 = ?", answer = "7", segmentPattern = SegmentPatterns.DIGIT_7, difficulty = 2),
        StickBuilderLevelContent(id = 17, equation = "4 + 4 = ?", answer = "8", segmentPattern = SegmentPatterns.DIGIT_8, difficulty = 2),
        StickBuilderLevelContent(id = 18, equation = "5 + 4 = ?", answer = "9", segmentPattern = SegmentPatterns.DIGIT_9, difficulty = 2),
        StickBuilderLevelContent(id = 19, equation = "3 + 3 = ?", answer = "6", segmentPattern = SegmentPatterns.DIGIT_6, difficulty = 2),
        StickBuilderLevelContent(id = 20, equation = "2 + 3 = ?", answer = "5", segmentPattern = SegmentPatterns.DIGIT_5, difficulty = 2),

        // Level 21-30: Subtraction problems
        StickBuilderLevelContent(id = 21, equation = "3 - 1 = ?", answer = "2", segmentPattern = SegmentPatterns.DIGIT_2, difficulty = 3),
        StickBuilderLevelContent(id = 22, equation = "5 - 2 = ?", answer = "3", segmentPattern = SegmentPatterns.DIGIT_3, difficulty = 3),
        StickBuilderLevelContent(id = 23, equation = "7 - 3 = ?", answer = "4", segmentPattern = SegmentPatterns.DIGIT_4, difficulty = 3),
        StickBuilderLevelContent(id = 24, equation = "9 - 4 = ?", answer = "5", segmentPattern = SegmentPatterns.DIGIT_5, difficulty = 3),
        StickBuilderLevelContent(id = 25, equation = "8 - 2 = ?", answer = "6", segmentPattern = SegmentPatterns.DIGIT_6, difficulty = 3),
        StickBuilderLevelContent(id = 26, equation = "9 - 2 = ?", answer = "7", segmentPattern = SegmentPatterns.DIGIT_7, difficulty = 3),
        StickBuilderLevelContent(id = 27, equation = "9 - 1 = ?", answer = "8", segmentPattern = SegmentPatterns.DIGIT_8, difficulty = 3),
        StickBuilderLevelContent(id = 28, equation = "5 - 5 = ?", answer = "0", segmentPattern = SegmentPatterns.DIGIT_0, difficulty = 3),
        StickBuilderLevelContent(id = 29, equation = "6 - 5 = ?", answer = "1", segmentPattern = SegmentPatterns.DIGIT_1, difficulty = 3),
        StickBuilderLevelContent(id = 30, equation = "4 - 3 = ?", answer = "1", segmentPattern = SegmentPatterns.DIGIT_1, difficulty = 3)
    )

    /**
     * Get level by ID
     */
    fun getLevelById(id: Int): StickBuilderLevelContent? {
        return levels.find { it.id == id }
    }

    /**
     * Get levels by difficulty
     */
    fun getLevelsByDifficulty(difficulty: Int): List<StickBuilderLevelContent> {
        return levels.filter { it.difficulty == difficulty }
    }

    /**
     * Get total number of levels
     */
    fun getTotalLevels(): Int = levels.size

    /**
     * Get next level
     */
    fun getNextLevel(currentId: Int): StickBuilderLevelContent? {
        val nextId = currentId + 1
        return if (nextId <= levels.size) getLevelById(nextId) else null
    }

    /**
     * Get previous level
     */
    fun getPreviousLevel(currentId: Int): StickBuilderLevelContent? {
        val prevId = currentId - 1
        return if (prevId >= 1) getLevelById(prevId) else null
    }
}
