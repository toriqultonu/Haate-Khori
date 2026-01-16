package com.example.alphabettracer.constants

/**
 * Game configuration constants.
 * These can be replaced with backend-fetched values in the future.
 */
object AppConfig {
    // App Configuration
    const val TOTAL_ALPHABET_LETTERS = 26

    // Counting Game
    object CountingGame {
        const val TOTAL_QUESTIONS = 10
        const val MIN_COUNT = 1
        const val MAX_COUNT = 10
        const val CONFETTI_STREAK_THRESHOLD = 3
    }

    // Pattern Game
    object PatternGame {
        const val TOTAL_QUESTIONS = 10
        const val AUTO_ADVANCE_DELAY_MS = 1500L
        const val CONFETTI_STREAK_THRESHOLD = 3
    }

    // Memory Match
    object MemoryMatch {
        const val EASY_PAIRS = 4
        const val MEDIUM_PAIRS = 6
        const val HARD_PAIRS = 8
        const val FLIP_DELAY_MS = 1000L
        const val TOTAL_PAIRS_PER_CATEGORY = 12
    }

    // Word Search
    object WordSearch {
        const val GRID_SIZE = 10
        const val MAX_GENERATION_ATTEMPTS = 50
        const val MAX_PLACEMENT_ATTEMPTS = 200
        const val MIN_WORD_LENGTH = 2
        const val MAX_WORD_LENGTH = 10
    }

    // Stick Builder
    object StickBuilder {
        const val TOTAL_LEVELS = 30
        const val TOTAL_STICKS = 8
    }

    // Tracing
    object Tracing {
        const val EXCELLENT_THRESHOLD = 0.85f
        const val GOOD_THRESHOLD = 0.60f
        const val STREAK_MILESTONE = 5
        const val MIN_COVERAGE_THRESHOLD = 0.3f
    }

    // Achievement Thresholds
    object Achievements {
        const val FIRST_STAR_THRESHOLD = 1
        const val FIVE_STARS_THRESHOLD = 5
        const val ALPHABET_MASTER_THRESHOLD = 26
        const val STREAK_CHAMPION_THRESHOLD = 5
        const val COLORFUL_ARTIST_THRESHOLD = 4
        const val SUPER_TRACER_THRESHOLD = 10
    }

    // Storage Keys
    object StorageKeys {
        const val LETTER_RESULTS_PREFIX = "letter_result_"
        const val TOTAL_STARS = "total_stars"
        const val UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"
        const val MAX_STREAK = "max_streak"
        const val COLORS_USED = "colors_used"
        const val WORD_SEARCH_PROGRESS = "word_search_progress"
        const val STICK_BUILDER_LEVEL = "stick_builder_level"
        const val COUNTING_HIGH_SCORE = "counting_high_score"
        const val MEMORY_BEST_MOVES = "memory_best_moves"
        const val PATTERN_HIGH_SCORE = "pattern_high_score"
    }

    // API Endpoints (for future backend integration)
    object Api {
        const val BASE_URL = "https://api.example.com/v1/"
        const val ALPHABET_DATA = "alphabet"
        const val WORD_SEARCH_TOPICS = "word-search/topics"
        const val PATTERN_DATA = "patterns"
        const val COUNTING_DATA = "counting"
        const val MEMORY_DATA = "memory"
        const val ACHIEVEMENTS = "achievements"
        const val USER_PROGRESS = "user/progress"
    }
}
