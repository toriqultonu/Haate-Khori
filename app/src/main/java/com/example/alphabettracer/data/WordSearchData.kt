package com.example.alphabettracer.data

import androidx.compose.ui.graphics.Color
import com.example.alphabettracer.model.WordSearchTopic
import com.example.alphabettracer.ui.theme.*

/**
 * Collection of word search topics with their associated words
 */
val wordSearchTopics = listOf(
    WordSearchTopic(
        id = "countries",
        name = "Countries",
        icon = "globe",
        color = FunBlue,
        words = listOf("INDIA", "CHINA", "JAPAN", "EGYPT", "FRANCE", "SPAIN", "ITALY", "BRAZIL")
    ),
    WordSearchTopic(
        id = "body_parts",
        name = "Body Parts",
        icon = "body",
        color = FunPink,
        words = listOf("HEAD", "HAND", "FOOT", "NOSE", "EYE", "EAR", "LEG", "ARM", "NECK", "FACE")
    ),
    WordSearchTopic(
        id = "vehicles",
        name = "Vehicles",
        icon = "car",
        color = FunOrange,
        words = listOf("CAR", "BUS", "BIKE", "BOAT", "TRAIN", "PLANE", "SHIP", "TRUCK")
    ),
    WordSearchTopic(
        id = "food",
        name = "Food",
        icon = "food",
        color = FunGreen,
        words = listOf("RICE", "BREAD", "FISH", "MEAT", "CAKE", "PIZZA", "SOUP", "FRUIT")
    ),
    WordSearchTopic(
        id = "animals",
        name = "Animals",
        icon = "animal",
        color = FunYellow,
        words = listOf("DOG", "CAT", "LION", "BEAR", "BIRD", "FISH", "DEER", "FROG", "DUCK")
    ),
    WordSearchTopic(
        id = "colors",
        name = "Colors",
        icon = "palette",
        color = FunPurple,
        words = listOf("RED", "BLUE", "GREEN", "YELLOW", "PINK", "BLACK", "WHITE", "ORANGE")
    ),
    WordSearchTopic(
        id = "fruits",
        name = "Fruits",
        icon = "fruit",
        color = FunRed,
        words = listOf("APPLE", "MANGO", "BANANA", "GRAPE", "ORANGE", "PEACH", "PLUM", "PEAR")
    ),
    WordSearchTopic(
        id = "family",
        name = "Family",
        icon = "family",
        color = FunTeal,
        words = listOf("MOM", "DAD", "BROTHER", "SISTER", "GRANDMA", "GRANDPA", "UNCLE", "AUNT")
    )
)

/**
 * Colors to use for highlighting found words
 */
val wordHighlightColors = listOf(
    Color(0xFFFFEB3B), // Yellow
    Color(0xFF90CAF9), // Light Blue
    Color(0xFFF48FB1), // Pink
    Color(0xFFA5D6A7), // Light Green
    Color(0xFFCE93D8), // Purple
    Color(0xFFFFCC80), // Orange
    Color(0xFF80DEEA), // Cyan
    Color(0xFFFFAB91)  // Deep Orange
)
