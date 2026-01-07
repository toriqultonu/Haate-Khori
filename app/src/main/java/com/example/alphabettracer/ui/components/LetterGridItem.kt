package com.example.alphabettracer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.model.MatchResult

@Composable
fun LetterGridItem(
    letter: Char,
    result: com.example.alphabettracer.model.MatchResult,
    onClick: () -> Unit
) {
    // Colors based on result: Green (Excellent), Yellow (Good), Red (Poor), White (None)
    val (backgroundColor, textColor, icon) = when (result) {
        com.example.alphabettracer.model.MatchResult.EXCELLENT -> Triple(Color(0xFF4CAF50), Color.White, "⭐")  // Green
        com.example.alphabettracer.model.MatchResult.GOOD -> Triple(Color(0xFFFFC107), Color(0xFF333333), "👍")  // Yellow
        com.example.alphabettracer.model.MatchResult.POOR -> Triple(Color(0xFFFF5722), Color.White, "🔄")  // Red/Orange
        MatchResult.NONE -> Triple(Color.White, Color(0xFF333333), "")  // White (not attempted)
    }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = letter.toString(),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                if (icon.isNotEmpty()) {
                    Text(icon, fontSize = 12.sp)
                }
            }
        }
    }
}