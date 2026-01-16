package com.example.alphabettracer.constants

/**
 * Centralized string constants for the app.
 * In future, these can be replaced with backend-fetched strings or localization.
 */
object AppStrings {
    // App Info
    const val APP_NAME = "Haate Khori"

    // Navigation
    object Navigation {
        const val BACK_TO_HOME = "Back to home"
        const val BACK_TO_LETTERS = "Back to letters"
        const val BACK_TO_TOPICS = "Back to topics"
        const val BACK_TO_CATEGORIES = "Back to Categories"
        const val PREVIOUS = "Previous"
        const val NEXT = "Next"
        const val BACK = "Back"
        const val BACK_HOME = "Back Home"
    }

    // Home Screen
    object Home {
        const val TITLE = "Let's Learn & Play!"
        const val PRACTICE_LETTERS = "Practice Letters"
        const val PRACTICE_SUBTITLE = "Trace A-Z with your finger"
        const val LETTERS_PRACTICED = "of letters practiced"
        const val FUN_GAMES = "Fun Games"
        const val QUICK_GAMES = "Quick Games"
    }

    // Letter Selection Screen
    object LetterSelection {
        const val TITLE = "Choose a Letter"
        const val YOUR_PROGRESS = "Your Progress"
        const val TAP_TO_START = "Tap any letter to start practicing!"
        const val GREAT_START = "Great start! Keep going!"
        const val ON_FIRE = "You're on fire!"
        const val ALMOST_THERE = "Almost there!"
        const val CLOSE_TO_MASTERY = "So close to mastery!"
        const val ALPHABET_MASTER = "You're an Alphabet Master!"
    }

    // Tracing Screen
    object Tracing {
        const val EXCELLENT = "Excellent!"
        const val GOOD_JOB = "Good job! Keep practicing!"
        const val KEEP_TRYING = "Keep trying! Trace more of the letter."
        const val STREAK = "Streak:"
        const val AMAZING_DIALOG_TITLE = "Amazing!"
        const val STREAK_MESSAGE = "You got a %d letter streak!"
        const val KEEP_GOING = "Keep Going!"
    }

    // Word Search
    object WordSearch {
        const val TITLE = "Word Search"
        const val TOPICS = "Topics"
        const val GAMES = "Games"
        const val PICK_TOPIC = "Pick a topic to play!"
        const val COMPLETED = "Completed"
        const val FIND_ALL_WORDS = "Find all %d words!"
        const val FOUND_PROGRESS = "Found: %d/%d"
        const val NEW_GAME = "New Game"
        const val BACK_TO_TOPICS = "Back to Topics"
        const val CONGRATULATIONS = "Congratulations!"
        const val ALL_WORDS_FOUND = "You found all words in %s!"
    }

    // Stick Builder
    object StickBuilder {
        const val TITLE = "Stick Builder"
        const val LEVEL_INDICATOR = "Level %d/%d"
        const val BUILD_ANSWER = "Build the answer!"
        const val CHECK = "CHECK"
        const val CORRECT = "Correct!"
        const val RESET = "Reset"
        const val TAP_TO_RETURN = "Tap sticks on board to return"
    }

    // Counting Game
    object Counting {
        const val TITLE = "Count & Learn!"
        const val QUESTION_COUNTER = "Question %d/%d"
        const val HOW_MANY = "How many do you see?"
        const val CORRECT_ANSWER = "Correct! The answer is %d"
        const val WRONG_ANSWER = "Oops! The answer was %d"
        const val GAME_COMPLETE = "Game Complete!"
        const val NEW_HIGH_SCORE = "New High Score!"
        const val HIGH_SCORE = "High Score: %d"
        const val PLAY_AGAIN = "Play Again"
        const val PERFECT = "Perfect! You're a counting champion!"
        const val GREAT_JOB = "Great job! Almost perfect!"
        const val GOOD_WORK = "Good work! Keep practicing!"
        const val NICE_TRY = "Nice try! Practice makes perfect!"
    }

    // Memory Match
    object MemoryMatch {
        const val TITLE = "Memory Match"
        const val SUBTITLE = "Find matching pairs!"
        const val MOVES = "Moves"
        const val TIME = "Time"
        const val PAIRS = "Pairs"
        const val CHOOSE_CATEGORY = "Choose a Category"
        const val CHOOSE_DIFFICULTY = "Choose Difficulty"
        const val CATEGORY_DISPLAY = "Category: %s %s"
        const val CONGRATULATIONS = "Congratulations!"
        const val PLAY_AGAIN = "Play Again"
        const val CHANGE_CATEGORY = "Change Category"
    }

    // Pattern Game
    object Pattern {
        const val TITLE = "Pattern Game"
        const val QUESTION_COUNTER = "Question %d/%d"
        const val WHAT_COMES_NEXT = "What comes next?"
        const val CHOOSE_ANSWER = "Choose your answer:"
        const val CORRECT = "Correct!"
        const val ANSWER_WAS = "The answer was:"
        const val GAME_COMPLETE = "Game Complete!"
        const val NEW_HIGH_SCORE = "New High Score!"
        const val HIGH_SCORE = "High Score: %d"
    }

    // Achievements
    object Achievements {
        const val TITLE = "Achievements"
        const val UNLOCKED = "Achievement Unlocked!"
        const val AWESOME = "Awesome!"
    }

    // Common
    object Common {
        const val SCORE = "Score"
        const val LOADING = "Loading..."
        const val ERROR = "Something went wrong"
        const val RETRY = "Retry"
        const val OK = "OK"
        const val CANCEL = "Cancel"
    }

    // Game Buttons
    object GameButtons {
        const val WORD_SEARCH = "Word Search"
        const val STICK_BUILDER = "Stick Builder"
        const val COUNT = "Count"
        const val MEMORY = "Memory"
        const val PATTERN = "Pattern"
    }

    // Difficulty Levels
    object Difficulty {
        const val EASY = "Easy"
        const val MEDIUM = "Medium"
        const val HARD = "Hard"
    }
}

/**
 * Emoji constants used throughout the app
 */
object AppEmojis {
    // Decorative
    const val STAR = "⭐"
    const val FIRE = "🔥"
    const val TROPHY = "🏆"
    const val MEDAL = "🏅"
    const val CELEBRATION = "🎉"
    const val GAME = "🎮"
    const val TARGET = "🎯"
    const val PALETTE = "🎨"
    const val PENCIL = "✏️"
    const val CHART = "📊"
    const val FINGER_UP = "👆"
    const val SUN = "🌞"
    const val CHECKMARK = "✅"
    const val LOCK = "🔒"
    const val QUESTION = "❓"

    // Games
    const val SEARCH = "🔍"
    const val WOOD = "🪵"
    const val NUMBERS = "🔢"
    const val BRAIN = "🧠"
    const val PUZZLE = "🧩"
    const val TIMER = "⏱️"
    const val LIGHT_BULB = "💡"

    // Letter emojis A-Z
    val LETTER_EMOJIS = mapOf(
        'A' to "🍎", 'B' to "🏀", 'C' to "🐱", 'D' to "🐕",
        'E' to "🐘", 'F' to "🐸", 'G' to "🍇", 'H' to "🏠",
        'I' to "🍦", 'J' to "🃏", 'K' to "🔑", 'L' to "🦁",
        'M' to "🐵", 'N' to "👃", 'O' to "🐙", 'P' to "🐷",
        'Q' to "👸", 'R' to "🌈", 'S' to "⭐", 'T' to "🐯",
        'U' to "☂️", 'V' to "🎻", 'W' to "🐳", 'X' to "❌",
        'Y' to "🪀", 'Z' to "🦓"
    )

    // Topic icons
    val TOPIC_ICONS = mapOf(
        "globe" to "🌍",
        "body" to "🧍",
        "car" to "🚗",
        "food" to "🍕",
        "paw" to "🐾",
        "palette" to "🎨",
        "apple" to "🍎",
        "family" to "👨‍👩‍👧‍👦"
    )
}
