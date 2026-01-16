package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.alphabetList
import com.example.alphabettracer.model.Achievement
import com.example.alphabettracer.model.MatchResult
import com.example.alphabettracer.ui.components.AchievementSection
import com.example.alphabettracer.ui.components.LetterGridItem
import kotlinx.coroutines.delay

@Composable
fun LetterSelectionScreen(
    letterResults: Map<Int, MatchResult>,
    unlockedAchievements: Set<Achievement>,
    onLetterSelected: (Int) -> Unit
) {
    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showGrid by remember { mutableStateOf(false) }

    // Trigger staggered animations
    LaunchedEffect(Unit) {
        showHeader = true
        delay(100)
        showProgress = true
        delay(150)
        showAchievements = true
        delay(200)
        showGrid = true
    }

    // Calculate progress
    val completedCount = letterResults.count { it.value.ordinal >= MatchResult.GOOD.ordinal }
    val excellentCount = letterResults.count { it.value == MatchResult.EXCELLENT }
    val progress = excellentCount.toFloat() / alphabetList.size

    // Animated progress value
    val animatedProgress by animateFloatAsState(
        targetValue = if (showProgress) progress else 0f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with animation
        AnimatedVisibility(
            visible = showHeader,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ABC", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Choose a Letter",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE)
                )
                Spacer(Modifier.width(12.dp))
                Text("abc", fontSize = 28.sp)
            }
        }

        // Progress Card with animation
        AnimatedVisibility(
            visible = showProgress,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(400)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📊", fontSize = 18.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Your Progress",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 16.sp)
                            Text(
                                " $excellentCount/${alphabetList.size}",
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp)),
                        color = Color(0xFF4CAF50),
                        trackColor = Color(0xFFE8F5E9)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when {
                            excellentCount == 0 -> "Tap any letter to start practicing!"
                            excellentCount < 5 -> "Great start! Keep going!"
                            excellentCount < 13 -> "You're on fire!"
                            excellentCount < 20 -> "Almost there!"
                            excellentCount < 26 -> "So close to mastery!"
                            else -> "You're an Alphabet Master!"
                        },
                        fontSize = 14.sp,
                        color = Color(0xFF6200EE),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Achievement section with animation
        AnimatedVisibility(
            visible = showAchievements,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it / 2 },
                animationSpec = tween(400)
            )
        ) {
            AchievementSection(
                unlockedAchievements = unlockedAchievements,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Letter grid with animation
        AnimatedVisibility(
            visible = showGrid,
            enter = fadeIn(tween(400))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(alphabetList) { index, item ->
                    val result = letterResults[index] ?: MatchResult.NONE
                    LetterGridItem(
                        letter = item.letter,
                        result = result,
                        onClick = { onLetterSelected(index) }
                    )
                }
            }
        }
    }
}
