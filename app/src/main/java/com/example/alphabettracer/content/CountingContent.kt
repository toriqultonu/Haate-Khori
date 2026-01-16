package com.example.alphabettracer.content

/**
 * Counting Game content data.
 * Contains emoji categories for the counting game.
 * Can be replaced with backend data in the future.
 */
object CountingContent {

    val categories: List<CountingCategoryContent> = listOf(
        CountingCategoryContent(
            id = "animals",
            name = "Animals",
            items = listOf("🐶", "🐱", "🐭", "🐹", "🐰", "🦊", "🐻", "🐼", "🐨", "🐯"),
            minCount = 1,
            maxCount = 10
        ),
        CountingCategoryContent(
            id = "fruits",
            name = "Fruits",
            items = listOf("🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🫐", "🍒"),
            minCount = 1,
            maxCount = 10
        ),
        CountingCategoryContent(
            id = "stars",
            name = "Stars",
            items = listOf("⭐", "🌟", "✨", "💫", "⚡", "🌙", "☀️", "🌈", "🌸", "🎀"),
            minCount = 1,
            maxCount = 10
        ),
        CountingCategoryContent(
            id = "hearts",
            name = "Hearts",
            items = listOf("❤️", "🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "💖", "💝"),
            minCount = 1,
            maxCount = 10
        ),
        CountingCategoryContent(
            id = "vehicles",
            name = "Vehicles",
            items = listOf("🚗", "🚕", "🚙", "🚌", "🚎", "🏎️", "🚓", "🚑", "🚒", "🚐"),
            minCount = 1,
            maxCount = 10
        )
    )

    /**
     * Get a random category
     */
    fun getRandomCategory(): CountingCategoryContent {
        return categories.random()
    }

    /**
     * Get a random emoji from a category
     */
    fun getRandomEmoji(categoryId: String): String {
        val category = categories.find { it.id == categoryId }
        return category?.items?.random() ?: "🔢"
    }

    /**
     * Get category by ID
     */
    fun getCategoryById(id: String): CountingCategoryContent? {
        return categories.find { it.id == id }
    }

    /**
     * Generate a counting question
     */
    fun generateQuestion(minCount: Int = 1, maxCount: Int = 10): Pair<List<String>, Int> {
        val category = getRandomCategory()
        val emoji = category.items.random()
        val count = (minCount..maxCount).random()
        val items = List(count) { emoji }
        return Pair(items, count)
    }

    /**
     * Generate wrong answer options (excluding the correct answer)
     */
    fun generateWrongOptions(correctAnswer: Int, count: Int = 3, range: IntRange = 1..10): List<Int> {
        val options = range.filter { it != correctAnswer }.shuffled().take(count)
        return options
    }
}
