package com.example.alphabettracer.content

/**
 * Pattern Game content data.
 * Contains pattern sequences for the "What comes next?" game.
 * Can be replaced with backend data in the future.
 */
object PatternContent {

    private val shapePatterns = listOf(
        PatternSequenceContent(
            id = "shape_1",
            category = "shapes",
            sequence = listOf("🔴", "🔵", "🔴", "🔵", "🔴"),
            answer = "🔵",
            options = listOf("🔵", "🔴", "🟢", "🟡"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "shape_2",
            category = "shapes",
            sequence = listOf("⭐", "⭐", "🌙", "⭐", "⭐"),
            answer = "🌙",
            options = listOf("🌙", "⭐", "☀️", "🌟"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "shape_3",
            category = "shapes",
            sequence = listOf("🔷", "🔶", "🔷", "🔶", "🔷"),
            answer = "🔶",
            options = listOf("🔶", "🔷", "🔴", "🟢"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "shape_4",
            category = "shapes",
            sequence = listOf("❤️", "💛", "💚", "❤️", "💛"),
            answer = "💚",
            options = listOf("💚", "❤️", "💛", "💙"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "shape_5",
            category = "shapes",
            sequence = listOf("🔺", "🔻", "🔺", "🔻", "🔺"),
            answer = "🔻",
            options = listOf("🔻", "🔺", "◀️", "▶️"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "shape_6",
            category = "shapes",
            sequence = listOf("⬛", "⬜", "⬛", "⬜", "⬛"),
            answer = "⬜",
            options = listOf("⬜", "⬛", "🔲", "🔳"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "shape_7",
            category = "shapes",
            sequence = listOf("🟡", "🟡", "🟠", "🟡", "🟡"),
            answer = "🟠",
            options = listOf("🟠", "🟡", "🔴", "🟢"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "shape_8",
            category = "shapes",
            sequence = listOf("🔵", "🟢", "🔵", "🟢", "🔵"),
            answer = "🟢",
            options = listOf("🟢", "🔵", "🟣", "🟡"),
            difficulty = 1
        )
    )

    private val numberPatterns = listOf(
        PatternSequenceContent(
            id = "number_1",
            category = "numbers",
            sequence = listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣"),
            answer = "6️⃣",
            options = listOf("6️⃣", "7️⃣", "5️⃣", "4️⃣"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "number_2",
            category = "numbers",
            sequence = listOf("2️⃣", "4️⃣", "6️⃣", "8️⃣", "🔟"),
            answer = "🔟",
            options = listOf("🔟", "9️⃣", "7️⃣", "5️⃣"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "number_3",
            category = "numbers",
            sequence = listOf("1️⃣", "1️⃣", "2️⃣", "2️⃣", "3️⃣"),
            answer = "3️⃣",
            options = listOf("3️⃣", "4️⃣", "2️⃣", "1️⃣"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "number_4",
            category = "numbers",
            sequence = listOf("5️⃣", "4️⃣", "3️⃣", "2️⃣", "1️⃣"),
            answer = "0️⃣",
            options = listOf("0️⃣", "1️⃣", "6️⃣", "2️⃣"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "number_5",
            category = "numbers",
            sequence = listOf("1️⃣", "3️⃣", "5️⃣", "7️⃣", "9️⃣"),
            answer = "9️⃣",
            options = listOf("9️⃣", "8️⃣", "🔟", "6️⃣"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "number_6",
            category = "numbers",
            sequence = listOf("0️⃣", "1️⃣", "0️⃣", "1️⃣", "0️⃣"),
            answer = "1️⃣",
            options = listOf("1️⃣", "0️⃣", "2️⃣", "3️⃣"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "number_7",
            category = "numbers",
            sequence = listOf("3️⃣", "6️⃣", "9️⃣", "3️⃣", "6️⃣"),
            answer = "9️⃣",
            options = listOf("9️⃣", "3️⃣", "🔟", "7️⃣"),
            difficulty = 2
        )
    )

    private val colorPatterns = listOf(
        PatternSequenceContent(
            id = "color_1",
            category = "colors",
            sequence = listOf("🔴", "🟠", "🟡", "🟢", "🔵"),
            answer = "🟣",
            options = listOf("🟣", "🔴", "⚫", "⚪"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "color_2",
            category = "colors",
            sequence = listOf("⚪", "⚫", "⚪", "⚫", "⚪"),
            answer = "⚫",
            options = listOf("⚫", "⚪", "🔴", "🔵"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "color_3",
            category = "colors",
            sequence = listOf("🔴", "🔴", "🔵", "🔵", "🔴"),
            answer = "🔴",
            options = listOf("🔴", "🔵", "🟢", "🟡"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "color_4",
            category = "colors",
            sequence = listOf("🟡", "🟢", "🔵", "🟡", "🟢"),
            answer = "🔵",
            options = listOf("🔵", "🟡", "🟢", "🔴"),
            difficulty = 2
        )
    )

    private val emojiPatterns = listOf(
        PatternSequenceContent(
            id = "emoji_1",
            category = "emoji",
            sequence = listOf("🐱", "🐶", "🐱", "🐶", "🐱"),
            answer = "🐶",
            options = listOf("🐶", "🐱", "🐭", "🐰"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "emoji_2",
            category = "emoji",
            sequence = listOf("🍎", "🍌", "🍊", "🍎", "🍌"),
            answer = "🍊",
            options = listOf("🍊", "🍎", "🍌", "🍇"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "emoji_3",
            category = "emoji",
            sequence = listOf("☀️", "🌙", "☀️", "🌙", "☀️"),
            answer = "🌙",
            options = listOf("🌙", "☀️", "⭐", "🌟"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "emoji_4",
            category = "emoji",
            sequence = listOf("🚗", "🚕", "🚙", "🚗", "🚕"),
            answer = "🚙",
            options = listOf("🚙", "🚗", "🚕", "🚌"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "emoji_5",
            category = "emoji",
            sequence = listOf("🌸", "🌺", "🌸", "🌺", "🌸"),
            answer = "🌺",
            options = listOf("🌺", "🌸", "🌻", "🌷"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "emoji_6",
            category = "emoji",
            sequence = listOf("⚽", "🏀", "⚽", "🏀", "⚽"),
            answer = "🏀",
            options = listOf("🏀", "⚽", "🏈", "⚾"),
            difficulty = 1
        ),
        PatternSequenceContent(
            id = "emoji_7",
            category = "emoji",
            sequence = listOf("🎈", "🎁", "🎂", "🎈", "🎁"),
            answer = "🎂",
            options = listOf("🎂", "🎈", "🎁", "🎉"),
            difficulty = 2
        ),
        PatternSequenceContent(
            id = "emoji_8",
            category = "emoji",
            sequence = listOf("🌲", "🌳", "🌲", "🌳", "🌲"),
            answer = "🌳",
            options = listOf("🌳", "🌲", "🌴", "🌵"),
            difficulty = 1
        )
    )

    /**
     * All pattern sequences combined
     */
    val sequences: List<PatternSequenceContent> by lazy {
        shapePatterns + numberPatterns + colorPatterns + emojiPatterns
    }

    /**
     * Get random patterns for a game
     */
    fun getRandomPatterns(count: Int): List<PatternSequenceContent> {
        return sequences.shuffled().take(count)
    }

    /**
     * Get patterns by difficulty
     */
    fun getPatternsByDifficulty(difficulty: Int): List<PatternSequenceContent> {
        return sequences.filter { it.difficulty == difficulty }
    }

    /**
     * Get patterns by category
     */
    fun getPatternsByCategory(category: String): List<PatternSequenceContent> {
        return sequences.filter { it.category == category }
    }
}
