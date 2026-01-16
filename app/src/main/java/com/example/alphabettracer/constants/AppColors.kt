package com.example.alphabettracer.constants

import androidx.compose.ui.graphics.Color

/**
 * Centralized color constants for the app.
 * Kid-friendly color palette with vibrant, cheerful colors.
 */
object AppColors {
    // Primary Brand Colors
    val Primary = Color(0xFF6200EE)
    val PrimaryLight = Color(0xFF9C4DCC)
    val PrimaryDark = Color(0xFF3700B3)

    // Accent Colors
    val Accent = Color(0xFF03DAC5)
    val AccentLight = Color(0xFF66FFF9)
    val AccentDark = Color(0xFF00A896)

    // Status Colors
    object Status {
        val Success = Color(0xFF4CAF50)
        val SuccessLight = Color(0xFFE8F5E9)
        val Warning = Color(0xFFFF9800)
        val WarningLight = Color(0xFFFFF3E0)
        val Error = Color(0xFFF44336)
        val ErrorLight = Color(0xFFFFEBEE)
        val Info = Color(0xFF2196F3)
        val InfoLight = Color(0xFFE3F2FD)
    }

    // Result Colors (for letter tracing)
    object Result {
        val Excellent = Color(0xFF4CAF50)  // Green
        val ExcellentBackground = Color(0xFFE8F5E9)
        val Good = Color(0xFFFFC107)  // Yellow/Amber
        val GoodBackground = Color(0xFFFFF8E1)
        val Poor = Color(0xFFFF5722)  // Deep Orange
        val PoorBackground = Color(0xFFFBE9E7)
        val None = Color.White
    }

    // Game Colors
    object Games {
        val WordSearch = Color(0xFF2196F3)  // Blue
        val StickBuilder = Color(0xFF8B4513)  // Brown
        val Counting = Color(0xFF4CAF50)  // Green
        val MemoryMatch = Color(0xFF9C27B0)  // Purple
        val Pattern = Color(0xFFFF9800)  // Orange
    }

    // UI Colors
    object UI {
        val CardBackground = Color.White
        val TextPrimary = Color(0xFF333333)
        val TextSecondary = Color(0xFF666666)
        val TextHint = Color(0xFF999999)
        val Divider = Color(0xFFE0E0E0)
        val Disabled = Color(0xFFBDBDBD)
    }

    // Background Gradients
    object Gradients {
        val BackgroundTop = Color(0xFFFFF8E1)  // Warm cream
        val BackgroundMiddle = Color(0xFFE3F2FD)  // Light blue
        val BackgroundBottom = Color(0xFFF3E5F5)  // Light purple
    }

    // Achievement Colors
    object Achievement {
        val Gold = Color(0xFFFFD700)
        val GoldLight = Color(0xFFFFE55C)
        val Silver = Color(0xFFC0C0C0)
        val Bronze = Color(0xFFCD7F32)
        val Locked = Color(0xFFE0E0E0)
    }

    // TopBar
    val TopBarBackground = Primary
    val TopBarContent = Color.White

    // Canvas Colors (for tracing)
    object Canvas {
        val Background = Color.White
        val LetterGuide = Color(0xFFE0E0E0)
        val GuideDots = Color(0xFF9C27B0)
        val StrokeDefault = Color(0xFF333333)
        val StrokeColors = listOf(
            Color(0xFF333333),  // Black
            Color(0xFF2196F3),  // Blue
            Color(0xFF4CAF50),  // Green
            Color(0xFFF44336),  // Red
            Color(0xFFFF9800),  // Orange
            Color(0xFF9C27B0)   // Purple
        )
    }

    // Confetti Colors
    val ConfettiColors = listOf(
        Color(0xFFFF6B6B),  // Red
        Color(0xFF4ECDC4),  // Teal
        Color(0xFFFFE66D),  // Yellow
        Color(0xFF95E1D3),  // Mint
        Color(0xFFF38181),  // Coral
        Color(0xFFAA96DA),  // Lavender
        Color(0xFFFCBAD3),  // Pink
        Color(0xFFA8D8EA)   // Sky Blue
    )

    // Memory Card Colors
    object MemoryCards {
        val CardBack = Color(0xFF6200EE)
        val CardFront = Color.White
        val MatchedOverlay = Color(0x404CAF50)
    }

    // Stick Builder
    object StickBuilder {
        val StickFill = Color(0xFF8B4513)  // Brown
        val StickStroke = Color(0xFF5D2E0C)  // Dark brown
        val SegmentEmpty = Color(0xFFE0E0E0)
        val SegmentFilled = Color(0xFF8B4513)
        val SegmentHover = Color(0xFF2196F3)
        val SegmentHint = Color(0xFF4CAF50)
    }

    // Word Search
    object WordSearch {
        val GridBackground = Color.White
        val LetterNormal = Color(0xFF333333)
        val LetterFound = Color(0xFF4CAF50)
        val SelectionLine = Color(0xFF6200EE)
        val SelectionLineFound = Color(0xFF4CAF50)
    }
}
