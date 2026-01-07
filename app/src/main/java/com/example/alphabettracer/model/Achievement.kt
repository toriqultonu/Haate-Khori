package com.example.alphabettracer.model

enum class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requirement: String
) {
    FIRST_STAR(
        id = "first_star",
        title = "First Star",
        description = "Earned your first star!",
        icon = "🌟",
        requirement = "Get your first EXCELLENT trace"
    ),
    HIGH_FIVE(
        id = "high_five",
        title = "High Five",
        description = "You're on a roll!",
        icon = "🖐️",
        requirement = "Get 5 EXCELLENT traces"
    ),
    PERFECT_TEN(
        id = "perfect_ten",
        title = "Perfect Ten",
        description = "Double digits!",
        icon = "🔟",
        requirement = "Get 10 EXCELLENT traces"
    ),
    ALPHABET_CHAMPION(
        id = "alphabet_champion",
        title = "Alphabet Champion",
        description = "Master of letters!",
        icon = "🏆",
        requirement = "Master all 26 letters"
    ),
    HOT_STREAK(
        id = "hot_streak",
        title = "Hot Streak",
        description = "You're on fire!",
        icon = "🔥",
        requirement = "Get 5 EXCELLENT traces in a row"
    ),
    RAINBOW_ARTIST(
        id = "rainbow_artist",
        title = "Rainbow Artist",
        description = "Colors are fun!",
        icon = "🌈",
        requirement = "Use all 6 colors"
    ),
    QUICK_LEARNER(
        id = "quick_learner",
        title = "Quick Learner",
        description = "Half way there!",
        icon = "📚",
        requirement = "Master 13 letters"
    )
}
