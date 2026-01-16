package com.example.alphabettracer.data

/**
 * Data for the Pattern Recognition Game feature
 * Contains pattern sequences for "What comes next?" puzzles
 */
data class PatternQuestion(
    val sequence: List<String>,
    val correctAnswer: String,
    val options: List<String>
)

/**
 * Pattern types for generating sequences
 */
enum class PatternType(val label: String) {
    SHAPES("Shapes"),
    COLORS("Colors"),
    NUMBERS("Numbers"),
    EMOJIS("Emojis")
}

// Shape patterns
private val shapePatterns = listOf(
    // Simple AB patterns
    listOf("🔴", "🔵", "🔴", "🔵") to listOf("🔴", "🔵", "🟢", "🟡"),
    listOf("⭐", "🌙", "⭐", "🌙") to listOf("⭐", "🌙", "☀️", "🌈"),
    listOf("🔺", "🔻", "🔺", "🔻") to listOf("🔺", "🔻", "⬛", "⬜"),
    listOf("🟦", "🟨", "🟦", "🟨") to listOf("🟦", "🟨", "🟩", "🟥"),

    // ABC patterns
    listOf("🔴", "🟡", "🔵", "🔴", "🟡") to listOf("🔵", "🟢", "🟣", "⚫"),
    listOf("⬛", "⬜", "🔳", "⬛", "⬜") to listOf("🔳", "⬛", "⬜", "🔲"),
    listOf("🌕", "🌗", "🌑", "🌕", "🌗") to listOf("🌑", "🌔", "🌖", "🌘"),

    // AABB patterns
    listOf("🔴", "🔴", "🔵", "🔵", "🔴", "🔴") to listOf("🔵", "🔴", "🟢", "🟡"),
    listOf("⭐", "⭐", "🌙", "🌙", "⭐", "⭐") to listOf("🌙", "⭐", "☀️", "🌈")
)

// Number patterns
private val numberPatterns = listOf(
    // Counting up
    listOf("1️⃣", "2️⃣", "3️⃣", "4️⃣") to listOf("5️⃣", "6️⃣", "3️⃣", "1️⃣"),
    listOf("2️⃣", "4️⃣", "6️⃣", "8️⃣") to listOf("🔟", "9️⃣", "7️⃣", "5️⃣"),
    listOf("1️⃣", "3️⃣", "5️⃣", "7️⃣") to listOf("9️⃣", "8️⃣", "6️⃣", "4️⃣"),

    // Counting down
    listOf("5️⃣", "4️⃣", "3️⃣", "2️⃣") to listOf("1️⃣", "6️⃣", "0️⃣", "7️⃣"),
    listOf("🔟", "9️⃣", "8️⃣", "7️⃣") to listOf("6️⃣", "5️⃣", "🔟", "1️⃣"),

    // Repeat patterns
    listOf("1️⃣", "2️⃣", "1️⃣", "2️⃣") to listOf("1️⃣", "3️⃣", "2️⃣", "4️⃣"),
    listOf("1️⃣", "1️⃣", "2️⃣", "2️⃣", "3️⃣", "3️⃣") to listOf("4️⃣", "5️⃣", "1️⃣", "3️⃣")
)

// Color patterns (using colored circles)
private val colorPatterns = listOf(
    listOf("🔴", "🟠", "🟡", "🟢") to listOf("🔵", "🟣", "⚫", "🔴"),
    listOf("🟥", "🟧", "🟨", "🟩") to listOf("🟦", "🟪", "⬛", "🟥"),
    listOf("❤️", "🧡", "💛", "💚") to listOf("💙", "💜", "🖤", "❤️"),

    // Rainbow repeat
    listOf("🔴", "🟠", "🟡", "🔴", "🟠") to listOf("🟡", "🟢", "🔵", "🟣")
)

// Emoji patterns (fun themes)
private val emojiPatterns = listOf(
    // Weather
    listOf("☀️", "🌧️", "☀️", "🌧️") to listOf("☀️", "🌈", "⛈️", "❄️"),
    listOf("🌞", "⛅", "🌧️", "🌞", "⛅") to listOf("🌧️", "🌈", "❄️", "🌞"),

    // Animals
    listOf("🐶", "🐱", "🐶", "🐱") to listOf("🐶", "🐰", "🐻", "🦁"),
    listOf("🐔", "🐤", "🐔", "🐤") to listOf("🐔", "🦆", "🐧", "🦅"),

    // Faces
    listOf("😀", "😢", "😀", "😢") to listOf("😀", "😴", "😡", "🤔"),
    listOf("😊", "😃", "😄", "😁") to listOf("😆", "😅", "🤣", "😂"),

    // Food
    listOf("🍎", "🍌", "🍎", "🍌") to listOf("🍎", "🍊", "🍇", "🍓"),
    listOf("🍕", "🍔", "🌭", "🍕", "🍔") to listOf("🌭", "🍟", "🍿", "🥤")
)

/**
 * Generates a pattern question based on type and difficulty
 */
fun generatePatternQuestion(type: PatternType? = null): PatternQuestion {
    val selectedType = type ?: PatternType.entries.random()

    val patterns = when (selectedType) {
        PatternType.SHAPES -> shapePatterns
        PatternType.COLORS -> colorPatterns
        PatternType.NUMBERS -> numberPatterns
        PatternType.EMOJIS -> emojiPatterns
    }

    val (sequence, options) = patterns.random()
    val correctAnswer = options.first()

    return PatternQuestion(
        sequence = sequence,
        correctAnswer = correctAnswer,
        options = options.shuffled()
    )
}

/**
 * Pattern game difficulty levels
 */
enum class PatternDifficulty(val sequenceLength: Int, val label: String) {
    EASY(4, "Easy"),      // 4 items in sequence
    MEDIUM(5, "Medium"),  // 5 items in sequence
    HARD(6, "Hard")       // 6 items in sequence
}

/**
 * Generates multiple pattern questions for a game session
 */
fun generatePatternGame(questionCount: Int = 10): List<PatternQuestion> {
    val questions = mutableListOf<PatternQuestion>()
    val types = PatternType.entries.toList()

    repeat(questionCount) { index ->
        // Cycle through pattern types for variety
        val type = types[index % types.size]
        questions.add(generatePatternQuestion(type))
    }

    return questions
}
