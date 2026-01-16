package com.example.alphabettracer.constants

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized dimension constants for consistent UI sizing.
 */
object AppDimensions {
    // Padding
    object Padding {
        val None = 0.dp
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 12.dp
        val Default = 16.dp
        val Large = 20.dp
        val ExtraLarge = 24.dp
        val Huge = 32.dp
    }

    // Spacing
    object Spacing {
        val ExtraSmall = 4.dp
        val Small = 8.dp
        val Medium = 12.dp
        val Default = 16.dp
        val Large = 20.dp
        val ExtraLarge = 24.dp
    }

    // Corner Radius
    object CornerRadius {
        val Small = 8.dp
        val Medium = 12.dp
        val Default = 16.dp
        val Large = 20.dp
        val Round = 50.dp
        val Circle = 100.dp
    }

    // Button Dimensions
    object Button {
        val MinHeight = 48.dp
        val DefaultHeight = 56.dp
        val LargeHeight = 60.dp
        val MiniGameHeight = 70.dp
        val IconSize = 24.dp
    }

    // Card Dimensions
    object Card {
        val MinHeight = 80.dp
        val Elevation = 4.dp
        val ElevationPressed = 2.dp
    }

    // Icon Sizes
    object Icon {
        val Small = 16.dp
        val Medium = 24.dp
        val Large = 32.dp
        val ExtraLarge = 48.dp
        val Huge = 64.dp
    }

    // Grid Layouts
    object Grid {
        val LetterGridColumns = 4
        val WordSearchTopicColumns = 2
        val CountingAnswerColumns = 2
        val WordSearchGridSize = 10
    }

    // Progress Indicator
    object Progress {
        val Height = 14.dp
        val CornerRadius = 7.dp
    }

    // Badge/Chip
    object Badge {
        val Size = 48.dp
        val SmallSize = 32.dp
    }

    // Text Sizes
    object TextSize {
        val ExtraSmall = 10.sp
        val Small = 12.sp
        val Medium = 14.sp
        val Default = 16.sp
        val Large = 18.sp
        val ExtraLarge = 20.sp
        val Title = 22.sp
        val Header = 24.sp
        val Display = 28.sp
        val Jumbo = 32.sp
        val Huge = 36.sp

        // Emoji sizes
        val EmojiSmall = 16.sp
        val EmojiMedium = 20.sp
        val EmojiDefault = 24.sp
        val EmojiLarge = 28.sp
        val EmojiHuge = 36.sp
    }

    // Stroke/Border
    object Stroke {
        val Thin = 1.dp
        val Default = 2.dp
        val Medium = 3.dp
        val Thick = 4.dp
    }

    // Animation Durations (in milliseconds)
    object AnimationDuration {
        const val ExtraFast = 100
        const val Fast = 150
        const val Default = 200
        const val Medium = 300
        const val Slow = 400
        const val VerySlow = 500
        const val ProgressBar = 1000
        const val Confetti = 3000
    }

    // Staggered Animation Delays
    object StaggerDelay {
        const val Fast = 50
        const val Default = 100
        const val Medium = 150
        const val Slow = 200
    }

    // Letter Tracing Canvas
    object TracingCanvas {
        val MinStrokeWidth = 8f
        val MaxStrokeWidth = 40f
        val DefaultStrokeWidth = 20f
    }

    // Memory Card
    object MemoryCard {
        val FlipDuration = 300
        val MatchCheckDelay = 1000L
    }

    // Stick Builder
    object StickBuilder {
        const val TotalSticks = 8
        const val SegmentThickness = 0.12f
        const val HitMargin = 0.06f
    }
}
