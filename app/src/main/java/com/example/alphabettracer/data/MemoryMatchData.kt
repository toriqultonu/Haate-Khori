package com.example.alphabettracer.data

/**
 * Data for the Memory Match Game feature
 * Contains card pairs for different categories
 */
data class MemoryCard(
    val id: Int,
    val content: String,
    val pairId: Int,
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

data class MemoryCategory(
    val name: String,
    val emoji: String,
    val color: Long,
    val pairs: List<String>
)

val memoryCategories = listOf(
    MemoryCategory(
        name = "Animals",
        emoji = "🐾",
        color = 0xFF4CAF50,
        pairs = listOf("🐶", "🐱", "🐰", "🐻", "🦁", "🐯", "🐮", "🐷", "🐸", "🐵", "🦊", "🐼")
    ),
    MemoryCategory(
        name = "Fruits",
        emoji = "🍎",
        color = 0xFFFF5722,
        pairs = listOf("🍎", "🍊", "🍋", "🍇", "🍓", "🍑", "🍒", "🍌", "🥝", "🍍", "🥭", "🍈")
    ),
    MemoryCategory(
        name = "Numbers",
        emoji = "🔢",
        color = 0xFF2196F3,
        pairs = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "0️⃣", "#️⃣")
    ),
    MemoryCategory(
        name = "Shapes",
        emoji = "🔷",
        color = 0xFF9C27B0,
        pairs = listOf("🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "⬛", "⬜", "🔶", "🔷", "🔺", "🔻")
    ),
    MemoryCategory(
        name = "Sports",
        emoji = "⚽",
        color = 0xFF795548,
        pairs = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏓", "🏸", "🥊", "⛳", "🎯", "🏆")
    ),
    MemoryCategory(
        name = "Food",
        emoji = "🍕",
        color = 0xFFE91E63,
        pairs = listOf("🍕", "🍔", "🌭", "🍟", "🍿", "🧁", "🍩", "🍪", "🎂", "🍦", "🥤", "🍫")
    )
)

/**
 * Memory game difficulty levels
 */
enum class MemoryDifficulty(val pairCount: Int, val gridColumns: Int, val label: String) {
    EASY(4, 2, "Easy (4 pairs)"),       // 2x4 grid = 8 cards
    MEDIUM(6, 3, "Medium (6 pairs)"),   // 3x4 grid = 12 cards
    HARD(8, 4, "Hard (8 pairs)"),       // 4x4 grid = 16 cards
    EXPERT(12, 4, "Expert (12 pairs)")  // 4x6 grid = 24 cards
}

/**
 * Generates a shuffled deck of memory cards for a game
 * @param category The category to use for card content
 * @param difficulty The difficulty level determining pair count
 * @return List of shuffled memory cards
 */
fun generateMemoryDeck(category: MemoryCategory, difficulty: MemoryDifficulty): List<MemoryCard> {
    val selectedPairs = category.pairs.take(difficulty.pairCount)
    val cards = mutableListOf<MemoryCard>()

    var cardId = 0
    selectedPairs.forEachIndexed { pairIndex, emoji ->
        // Create two cards for each pair
        cards.add(MemoryCard(id = cardId++, content = emoji, pairId = pairIndex))
        cards.add(MemoryCard(id = cardId++, content = emoji, pairId = pairIndex))
    }

    return cards.shuffled()
}

/**
 * Calculates star rating based on moves and time
 */
fun calculateMemoryStars(moves: Int, optimalMoves: Int, timeSeconds: Int): Int {
    val moveRatio = optimalMoves.toFloat() / moves
    return when {
        moveRatio >= 0.9f && timeSeconds < 60 -> 3  // Excellent
        moveRatio >= 0.6f && timeSeconds < 120 -> 2 // Good
        else -> 1                                    // Completed
    }
}
