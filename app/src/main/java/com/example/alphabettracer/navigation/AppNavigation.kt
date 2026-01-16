package com.example.alphabettracer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

/**
 * Navigation destinations/routes for the app.
 * Using sealed class for type-safe navigation.
 */
sealed class Screen(val route: String) {
    // Main screens
    data object Home : Screen("home")
    data object LetterSelection : Screen("letter_selection")
    data object Tracing : Screen("tracing/{letterIndex}") {
        fun createRoute(letterIndex: Int) = "tracing/$letterIndex"
    }

    // Word Search
    data object WordSearchTopics : Screen("word_search_topics")
    data object WordSearchGame : Screen("word_search_game/{topicId}") {
        fun createRoute(topicId: String) = "word_search_game/$topicId"
    }

    // Other games
    data object StickBuilder : Screen("stick_builder")
    data object CountingGame : Screen("counting_game")
    data object MemoryMatch : Screen("memory_match")
    data object PatternGame : Screen("pattern_game")
}

/**
 * Navigation arguments keys
 */
object NavArgs {
    const val LETTER_INDEX = "letterIndex"
    const val TOPIC_ID = "topicId"
}

/**
 * Extension functions for NavController to simplify navigation
 */
fun NavController.navigateToHome() {
    navigate(Screen.Home.route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateToLetterSelection() {
    navigate(Screen.LetterSelection.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToTracing(letterIndex: Int) {
    navigate(Screen.Tracing.createRoute(letterIndex)) {
        launchSingleTop = true
    }
}

fun NavController.navigateToWordSearchTopics() {
    navigate(Screen.WordSearchTopics.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToWordSearchGame(topicId: String) {
    navigate(Screen.WordSearchGame.createRoute(topicId)) {
        launchSingleTop = true
    }
}

fun NavController.navigateToStickBuilder() {
    navigate(Screen.StickBuilder.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToCountingGame() {
    navigate(Screen.CountingGame.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToMemoryMatch() {
    navigate(Screen.MemoryMatch.route) {
        launchSingleTop = true
    }
}

fun NavController.navigateToPatternGame() {
    navigate(Screen.PatternGame.route) {
        launchSingleTop = true
    }
}

/**
 * Get the display title for the current route
 */
fun getScreenTitle(route: String?): String {
    return when {
        route == null -> "Haate Khori"
        route == Screen.Home.route -> "Haate Khori"
        route == Screen.LetterSelection.route -> "Choose a Letter"
        route.startsWith("tracing/") -> "Practice"
        route == Screen.WordSearchTopics.route -> "Word Search"
        route.startsWith("word_search_game/") -> "Word Search"
        route == Screen.StickBuilder.route -> "Stick Builder"
        route == Screen.CountingGame.route -> "Count & Learn"
        route == Screen.MemoryMatch.route -> "Memory Match"
        route == Screen.PatternGame.route -> "Pattern Game"
        else -> "Haate Khori"
    }
}

/**
 * Check if back button should be shown for a route
 */
fun shouldShowBackButton(route: String?): Boolean {
    return route != null && route != Screen.Home.route
}

/**
 * Get the back destination for a route
 */
fun getBackDestination(route: String?): String {
    return when {
        route == null -> Screen.Home.route
        route == Screen.LetterSelection.route -> Screen.Home.route
        route.startsWith("tracing/") -> Screen.LetterSelection.route
        route == Screen.WordSearchTopics.route -> Screen.Home.route
        route.startsWith("word_search_game/") -> Screen.WordSearchTopics.route
        route == Screen.StickBuilder.route -> Screen.Home.route
        route == Screen.CountingGame.route -> Screen.Home.route
        route == Screen.MemoryMatch.route -> Screen.Home.route
        route == Screen.PatternGame.route -> Screen.Home.route
        else -> Screen.Home.route
    }
}
