package com.example.alphabettracer.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class DrawStroke(
    val points: List<Offset>,
    val color: Color,
    val width: Float
)
