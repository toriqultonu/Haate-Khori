package com.example.alphabettracer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Represents a word search topic/category
 */
data class WordSearchTopic(
    val id: String,
    val name: String,
    val icon: String,
    val color: Color,
    val words: List<String>
)

/**
 * Represents a found word in the puzzle
 */
data class FoundWord(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    val color: Color
)

/**
 * Represents a cell selection during word finding
 */
data class CellSelection(
    val row: Int,
    val col: Int
)

/**
 * Direction for word placement in the grid
 */
enum class WordDirection {
    HORIZONTAL,      // Left to right
    VERTICAL,        // Top to bottom
    DIAGONAL_DOWN,   // Top-left to bottom-right
    DIAGONAL_UP      // Bottom-left to top-right
}

/**
 * Represents a placed word in the grid
 */
data class PlacedWord(
    val word: String,
    val startRow: Int,
    val startCol: Int,
    val direction: WordDirection
)
