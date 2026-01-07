package com.example.alphabettracer.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.model.Achievement

@Composable
fun AchievementBadge(
    achievement: Achievement,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUnlocked) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badge_scale"
    )

    Column(
        modifier = modifier
            .scale(scale)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = if (isUnlocked) {
                Color(0xFFFFD700)  // Gold for unlocked
            } else {
                Color(0xFFE0E0E0)  // Gray for locked
            },
            shadowElevation = if (isUnlocked) 4.dp else 0.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = if (isUnlocked) achievement.icon else "🔒",
                    fontSize = 24.sp
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = achievement.title,
            fontSize = 10.sp,
            fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
            color = if (isUnlocked) Color(0xFF333333) else Color.Gray,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AchievementSection(
    unlockedAchievements: Set<Achievement>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "🏅 Achievements (${unlockedAchievements.size}/${Achievement.entries.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Achievement.entries.forEach { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        isUnlocked = achievement in unlockedAchievements,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }
    }
}
