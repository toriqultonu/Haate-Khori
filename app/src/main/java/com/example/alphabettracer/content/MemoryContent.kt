package com.example.alphabettracer.content

/**
 * Memory Match game content data.
 * Contains card categories and pairs for the memory matching game.
 * Can be replaced with backend data in the future.
 */
object MemoryContent {

    val categories: List<MemoryCategoryContent> = listOf(
        MemoryCategoryContent(
            id = "animals",
            name = "Animals",
            emoji = "🐾",
            color = 0xFF4CAF50,  // Green
            pairs = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯", "🦁", "🐮")
        ),
        MemoryCategoryContent(
            id = "fruits",
            name = "Fruits",
            emoji = "🍎",
            color = 0xFFE91E63,  // Pink
            pairs = listOf("🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍒", "🥝", "🍑")
        ),
        MemoryCategoryContent(
            id = "numbers",
            name = "Numbers",
            emoji = "🔢",
            color = 0xFF2196F3,  // Blue
            pairs = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "0️⃣", "#️⃣")
        ),
        MemoryCategoryContent(
            id = "shapes",
            name = "Shapes",
            emoji = "🔷",
            color = 0xFF9C27B0,  // Purple
            pairs = listOf("⭐", "❤️", "🔶", "🔷", "🔴", "🟢", "🟡", "🟣", "⬛", "⬜", "🔺", "🔻")
        ),
        MemoryCategoryContent(
            id = "sports",
            name = "Sports",
            emoji = "⚽",
            color = 0xFFFF9800,  // Orange
            pairs = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🏉", "🎱", "🏓", "🏸", "🥊", "⛳")
        ),
        MemoryCategoryContent(
            id = "food",
            name = "Food",
            emoji = "🍕",
            color = 0xFF795548,  // Brown
            pairs = listOf("🍕", "🍔", "🍟", "🌭", "🍿", "🧁", "🍩", "🍪", "🎂", "🍰", "🍫", "🍬")
        )
    )

    /**
     * Difficulty levels with number of pairs
     */
    object Difficulty {
        const val EASY = 4
        const val MEDIUM = 6
        const val HARD = 8
    }

    /**
     * Get category by ID
     */
    fun getCategoryById(id: String): MemoryCategoryContent? {
        return categories.find { it.id == id }
    }

    /**
     * Get shuffled card pairs for a game
     */
    fun getGameCards(categoryId: String, pairCount: Int): List<String> {
        val category = getCategoryById(categoryId) ?: return emptyList()
        val selectedPairs = category.pairs.shuffled().take(pairCount)
        // Duplicate each card to make pairs, then shuffle
        return (selectedPairs + selectedPairs).shuffled()
    }

    /**
     * Get all categories
     */
    fun getAllCategories(): List<MemoryCategoryContent> = categories

    /**
     * Calculate grid dimensions based on pair count
     */
    fun getGridDimensions(pairCount: Int): Pair<Int, Int> {
        val totalCards = pairCount * 2
        return when (totalCards) {
            8 -> Pair(2, 4)   // Easy: 2 rows x 4 columns
            12 -> Pair(3, 4)  // Medium: 3 rows x 4 columns
            16 -> Pair(4, 4)  // Hard: 4 rows x 4 columns
            else -> Pair(4, (totalCards + 3) / 4)
        }
    }
}
