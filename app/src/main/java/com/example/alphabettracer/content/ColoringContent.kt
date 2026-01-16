package com.example.alphabettracer.content

import androidx.compose.ui.graphics.Color

/**
 * Coloring Book content data.
 * Contains shape definitions and color palettes for the coloring feature.
 */
object ColoringContent {

    /**
     * Available colors for coloring - kid-friendly vibrant palette
     */
    val colorPalette: List<ColorOption> = listOf(
        ColorOption("red", Color(0xFFE53935), "Red"),
        ColorOption("orange", Color(0xFFFF9800), "Orange"),
        ColorOption("yellow", Color(0xFFFFEB3B), "Yellow"),
        ColorOption("green", Color(0xFF4CAF50), "Green"),
        ColorOption("light_blue", Color(0xFF03A9F4), "Light Blue"),
        ColorOption("blue", Color(0xFF2196F3), "Blue"),
        ColorOption("purple", Color(0xFF9C27B0), "Purple"),
        ColorOption("pink", Color(0xFFE91E63), "Pink"),
        ColorOption("brown", Color(0xFF795548), "Brown"),
        ColorOption("black", Color(0xFF333333), "Black")
    )

    /**
     * Coloring pages with different shapes/drawings
     */
    val coloringPages: List<ColoringPage> = listOf(
        // Simple shapes for beginners
        ColoringPage(
            id = "star",
            name = "Star",
            emoji = "⭐",
            difficulty = 1
        ),
        ColoringPage(
            id = "heart",
            name = "Heart",
            emoji = "❤️",
            difficulty = 1
        ),
        ColoringPage(
            id = "sun",
            name = "Sun",
            emoji = "☀️",
            difficulty = 1
        ),
        ColoringPage(
            id = "flower",
            name = "Flower",
            emoji = "🌸",
            difficulty = 2
        ),
        ColoringPage(
            id = "butterfly",
            name = "Butterfly",
            emoji = "🦋",
            difficulty = 2
        ),
        ColoringPage(
            id = "house",
            name = "House",
            emoji = "🏠",
            difficulty = 2
        ),
        ColoringPage(
            id = "fish",
            name = "Fish",
            emoji = "🐟",
            difficulty = 2
        ),
        ColoringPage(
            id = "car",
            name = "Car",
            emoji = "🚗",
            difficulty = 2
        ),
        ColoringPage(
            id = "tree",
            name = "Tree",
            emoji = "🌳",
            difficulty = 1
        ),
        ColoringPage(
            id = "rainbow",
            name = "Rainbow",
            emoji = "🌈",
            difficulty = 3
        ),
        ColoringPage(
            id = "rocket",
            name = "Rocket",
            emoji = "🚀",
            difficulty = 2
        ),
        ColoringPage(
            id = "ice_cream",
            name = "Ice Cream",
            emoji = "🍦",
            difficulty = 2
        )
    )

    fun getPageById(id: String): ColoringPage? = coloringPages.find { it.id == id }

    fun getPagesByDifficulty(difficulty: Int): List<ColoringPage> =
        coloringPages.filter { it.difficulty == difficulty }

    fun getColorByName(name: String): Color? =
        colorPalette.find { it.id == name }?.color
}

/**
 * Color option for the palette
 */
data class ColorOption(
    val id: String,
    val color: Color,
    val name: String
)

/**
 * A coloring page that displays an outline shape for kids to color
 */
data class ColoringPage(
    val id: String,
    val name: String,
    val emoji: String,
    val difficulty: Int
)
