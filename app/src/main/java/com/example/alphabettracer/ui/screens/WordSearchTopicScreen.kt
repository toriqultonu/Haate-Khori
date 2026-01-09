package com.example.alphabettracer.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.WordSearchStorage
import com.example.alphabettracer.data.wordSearchTopics
import com.example.alphabettracer.model.WordSearchTopic

@Composable
fun WordSearchTopicScreen(
    onTopicSelected: (WordSearchTopic) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val completedCount = WordSearchStorage.getCompletedTopicsCount(context)
    val totalGames = WordSearchStorage.getTotalGamesCompleted(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8E1),
                        Color(0xFFE3F2FD)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔍", fontSize = 28.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Word Search",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE)
            )
            Spacer(Modifier.width(8.dp))
            Text("🎯", fontSize = 28.sp)
        }

        // Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "🏆",
                    value = "$completedCount/${wordSearchTopics.size}",
                    label = "Topics"
                )
                StatItem(
                    icon = "🎮",
                    value = "$totalGames",
                    label = "Games"
                )
            }
        }

        // Instruction
        Row(
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👆", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Pick a topic to play!",
                fontSize = 16.sp,
                color = Color(0xFF6200EE),
                fontWeight = FontWeight.Medium
            )
        }

        // Topic Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(wordSearchTopics) { topic ->
                val isCompleted = WordSearchStorage.isTopicCompleted(context, topic.id)
                TopicCard(
                    topic = topic,
                    isCompleted = isCompleted,
                    onClick = { onTopicSelected(topic) }
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 24.sp)
        Text(
            value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF4CAF50)
        )
        Text(
            label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun TopicCard(
    topic: WordSearchTopic,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon circle with topic color
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(topic.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getTopicIcon(topic.icon),
                    fontSize = 28.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            // Topic name
            Text(
                text = topic.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = topic.color,
                textAlign = TextAlign.Center
            )

            // Word count
            Text(
                text = "${topic.words.size} words",
                fontSize = 12.sp,
                color = Color.Gray
            )

            // Completed indicator
            if (isCompleted) {
                Text(
                    text = "✅ Completed",
                    fontSize = 11.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getTopicIcon(iconName: String): String {
    return when (iconName) {
        "globe" -> "🌍"
        "body" -> "🧍"
        "car" -> "🚗"
        "food" -> "🍽️"
        "animal" -> "🐾"
        "palette" -> "🎨"
        "fruit" -> "🍎"
        "family" -> "👨‍👩‍👧‍👦"
        else -> "📝"
    }
}
