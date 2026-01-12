package com.example.alphabettracer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun LetterGridScreen(
    letterResults: Map<Int, MatchResult>,
    unlockedAchievements: Set<Achievement>,
    onLetterSelected: (Int) -> Unit,
    onWordSearchClicked: () -> Unit = {},
    onStickBuilderClicked: () -> Unit = {}
) {
    // Count letters with at least GOOD result for progress
    val completedCount = letterResults.count { it.value.ordinal >= MatchResult.GOOD.ordinal }
    val excellentCount = letterResults.count { it.value == MatchResult.EXCELLENT }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Fun decorative header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🎨", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Let's Learn ABC!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
            Spacer(Modifier.width(8.dp))
            Text("✏️", fontSize = 24.sp)
        }

        // Progress indicator
        val progress = excellentCount.toFloat() / alphabetList.size
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
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE8F5E9)
                )
                // Encouraging message based on progress
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when {
                        excellentCount == 0 -> "🌟 Start your adventure!"
                        excellentCount < 5 -> "🚀 Great start! Keep going!"
                        excellentCount < 13 -> "🔥 You're on fire!"
                        excellentCount < 20 -> "💪 Almost there!"
                        excellentCount < 26 -> "🏆 So close to mastery!"
                        else -> "🎉 You're an Alphabet Master!"
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Achievement section
        AchievementSection(
            unlockedAchievements = unlockedAchievements,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Word Search Button
        Button(
            onClick = onWordSearchClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2196F3)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🔍", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Play Word Search",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text("🎯", fontSize = 24.sp)
        }

        // Stick Builder Button
        Button(
            onClick = onStickBuilderClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B4513)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("🪵", fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Stick Builder",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text("🔢", fontSize = 24.sp)
        }

        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👆", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Tap a letter to practice!",
                fontSize = 16.sp,
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Medium
            )
        }

        // Letter grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
