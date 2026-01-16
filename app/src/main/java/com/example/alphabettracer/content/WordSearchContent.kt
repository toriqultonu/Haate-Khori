package com.example.alphabettracer.content

/**
 * Word Search game content data.
 * Contains topics and word lists for the word search puzzle.
 * Can be replaced with backend data in the future.
 */
object WordSearchContent {

    val topics: List<WordSearchTopicContent> = listOf(
        WordSearchTopicContent(
            id = "countries",
            name = "Countries",
            icon = "globe",
            words = listOf("INDIA", "CHINA", "JAPAN", "EGYPT", "FRANCE", "SPAIN", "ITALY", "BRAZIL"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "body_parts",
            name = "Body Parts",
            icon = "body",
            words = listOf("HEAD", "HAND", "FOOT", "NOSE", "EYE", "EAR", "LEG", "ARM", "NECK", "FACE"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "vehicles",
            name = "Vehicles",
            icon = "car",
            words = listOf("CAR", "BUS", "BIKE", "BOAT", "TRAIN", "PLANE", "SHIP", "TRUCK"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "food",
            name = "Food",
            icon = "food",
            words = listOf("RICE", "BREAD", "FISH", "MEAT", "CAKE", "PIZZA", "SOUP", "FRUIT"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "animals",
            name = "Animals",
            icon = "paw",
            words = listOf("DOG", "CAT", "LION", "BEAR", "BIRD", "FISH", "DEER", "FROG", "DUCK"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "colors",
            name = "Colors",
            icon = "palette",
            words = listOf("RED", "BLUE", "GREEN", "YELLOW", "PINK", "BLACK", "WHITE", "ORANGE"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "fruits",
            name = "Fruits",
            icon = "apple",
            words = listOf("APPLE", "MANGO", "BANANA", "GRAPE", "ORANGE", "PEACH", "PLUM", "PEAR"),
            difficulty = 1
        ),
        WordSearchTopicContent(
            id = "family",
            name = "Family",
            icon = "family",
            words = listOf("MOM", "DAD", "BROTHER", "SISTER", "GRANDMA", "GRANDPA", "UNCLE", "AUNT"),
            difficulty = 1
        )
    )

    /**
     * Icon mappings for topic icons
     */
    val topicIcons: Map<String, String> = mapOf(
        "globe" to "🌍",
        "body" to "🧍",
        "car" to "🚗",
        "food" to "🍕",
        "paw" to "🐾",
        "palette" to "🎨",
        "apple" to "🍎",
        "family" to "👨‍👩‍👧‍👦"
    )

    fun getTopicById(id: String): WordSearchTopicContent? {
        return topics.find { it.id == id }
    }

    fun getIconEmoji(iconName: String): String {
        return topicIcons[iconName] ?: "📝"
    }

    fun getTopicsByDifficulty(difficulty: Int): List<WordSearchTopicContent> {
        return topics.filter { it.difficulty == difficulty }
    }
}
