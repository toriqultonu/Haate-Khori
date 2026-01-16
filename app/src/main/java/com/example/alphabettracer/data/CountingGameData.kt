package com.example.alphabettracer.data

/**
 * Data for the Counting Game feature
 * Contains emoji collections for different categories
 */
data class CountingCategory(
    val name: String,
    val emoji: String,
    val items: List<String>
)

val countingCategories = listOf(
    CountingCategory(
        name = "Animals",
        emoji = "🐾",
        items = listOf("🐶", "🐱", "🐰", "🐻", "🦁", "🐯", "🐮", "🐷", "🐸", "🐵")
    ),
    CountingCategory(
        name = "Fruits",
        emoji = "🍎",
        items = listOf("🍎", "🍊", "🍋", "🍇", "🍓", "🍑", "🍒", "🍌", "🥝", "🍍")
    ),
    CountingCategory(
        name = "Stars",
        emoji = "⭐",
        items = listOf("⭐", "🌟", "✨", "💫", "🌠", "⭐", "🌟", "✨", "💫", "🌠")
    ),
    CountingCategory(
        name = "Hearts",
        emoji = "❤️",
        items = listOf("❤️", "💛", "💚", "💙", "💜", "🧡", "💖", "💗", "💕", "💝")
    ),
    CountingCategory(
        name = "Vehicles",
        emoji = "🚗",
        items = listOf("🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐")
    )
)

/**
 * Generates a counting question with a target count and random emojis
 * @param minCount Minimum number of items (inclusive)
 * @param maxCount Maximum number of items (inclusive)
 * @return Pair of (list of emojis, correct count)
 */
fun generateCountingQuestion(minCount: Int = 1, maxCount: Int = 10): Pair<List<String>, Int> {
    val category = countingCategories.random()
    val count = (minCount..maxCount).random()
    val emojis = (1..count).map { category.items.random() }
    return Pair(emojis, count)
}

/**
 * Generates wrong answer options for a counting question
 * @param correctAnswer The correct count
 * @param numOptions Total number of options including correct answer
 * @return Shuffled list of answer options
 */
fun generateAnswerOptions(correctAnswer: Int, numOptions: Int = 4): List<Int> {
    val options = mutableSetOf(correctAnswer)

    // Generate wrong answers close to correct answer
    val possibleWrong = mutableListOf<Int>()
    for (i in 1..10) {
        if (i != correctAnswer) possibleWrong.add(i)
    }

    // Prefer answers close to correct answer
    possibleWrong.sortBy { kotlin.math.abs(it - correctAnswer) }

    for (wrong in possibleWrong) {
        if (options.size >= numOptions) break
        options.add(wrong)
    }

    return options.shuffled()
}

// Difficulty levels for counting game
enum class CountingDifficulty(val minCount: Int, val maxCount: Int, val label: String) {
    EASY(1, 5, "Easy (1-5)"),
    MEDIUM(1, 10, "Medium (1-10)"),
    HARD(5, 15, "Hard (5-15)")
}
