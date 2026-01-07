package com.example.alphabettracer.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.alphabetList
import com.example.alphabettracer.model.MatchResult
import com.example.alphabettracer.ui.components.ConfettiAnimation
import com.example.alphabettracer.ui.components.TracingCanvas

@Composable
fun TracingScreen(
    currentIndex: Int,
    userStreak: Int,
    onStreakUpdate: (Int) -> Unit,
    onResultSaved: (Int, MatchResult) -> Unit,
    onColorUsed: (Int) -> Unit,
    onNavigate: (Int) -> Unit
) {
    val context = LocalContext.current
    val currentLetter = alphabetList[currentIndex]
    var showCongratsDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }

    // Confetti overlay - shows on top of everything
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Letter info card with fun styling
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Big letter display with gradient-like effect
                Surface(
                    modifier = Modifier.size(75.dp),
                    shape = CircleShape,
                    color = Color(0xFF6200EE),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = currentLetter.letter.toString(),
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = currentLetter.word,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (currentLetter.letter) {
                                'A' -> "🍎"
                                'B' -> "🏀"
                                'C' -> "🐱"
                                'D' -> "🐕"
                                'E' -> "🐘"
                                'F' -> "🐟"
                                'G' -> "🦒"
                                'H' -> "🏠"
                                'I' -> "🍦"
                                'J' -> "🍮"
                                'K' -> "🪁"
                                'L' -> "🦁"
                                'M' -> "🐵"
                                'N' -> "🪺"
                                'O' -> "🍊"
                                'P' -> "🐧"
                                'Q' -> "👸"
                                'R' -> "🌈"
                                'S' -> "☀️"
                                'T' -> "🐯"
                                'U' -> "☂️"
                                'V' -> "🎻"
                                'W' -> "🐋"
                                'X' -> "🎸"
                                'Y' -> "🐃"
                                'Z' -> "🦓"
                                else -> ""
                            },
                            fontSize = 24.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFF6200EE).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = currentLetter.sentence,
                            fontSize = 14.sp,
                            color = Color(0xFF6200EE),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Streak indicator
        AnimatedVisibility(
            visible = userStreak > 0,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = Color(0xFFFF9800).copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Streak: $userStreak",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                }
            }
        }

        // Tracing canvas
        TracingCanvas(
            letter = currentLetter.letter,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            onColorSelected = { colorIndex -> onColorUsed(colorIndex) },
            onCheckResult = { result ->
                // Save result (only saves if better than existing)
                if (result != MatchResult.NONE) {
                    onResultSaved(currentIndex, result)
                }

                when (result) {
                    MatchResult.EXCELLENT -> {
                        val newStreak = userStreak + 1
                        onStreakUpdate(newStreak)
                        showConfetti = true  // Trigger confetti celebration!
                        if (newStreak > 0 && newStreak % 5 == 0) {
                            showCongratsDialog = true
                        }
                        Toast.makeText(context, "Excellent! ⭐", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.GOOD -> {
                        onStreakUpdate(0) // Reset streak on non-excellent
                        Toast.makeText(context, "Good job! Keep practicing!", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.POOR -> {
                        onStreakUpdate(0) // Reset streak
                        Toast.makeText(context, "Keep trying! Trace more of the letter.", Toast.LENGTH_SHORT).show()
                    }
                    MatchResult.NONE -> {
                        // Do nothing
                    }
                }
            }
        )

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (currentIndex > 0) {
                        onNavigate(currentIndex - 1)
                    }
                },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                Spacer(Modifier.width(8.dp))
                Text("Previous")
            }

            Button(
                onClick = {
                    if (currentIndex < alphabetList.size - 1) {
                        onNavigate(currentIndex + 1)
                    }
                },
                enabled = currentIndex < alphabetList.size - 1,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Next")
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
            }
        }
    }

        // Confetti animation overlay
        ConfettiAnimation(
            isPlaying = showConfetti,
            onAnimationEnd = { showConfetti = false },
            modifier = Modifier.fillMaxSize()
        )
    }

    // Congrats dialog
    if (showCongratsDialog) {
        AlertDialog(
            onDismissRequest = { showCongratsDialog = false },
            title = {
                Text("🎉 Amazing!", textAlign = TextAlign.Center)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "You got a $userStreak letter streak!",
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("🏆", fontSize = 48.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCongratsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Keep Going!")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

