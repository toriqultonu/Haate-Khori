package com.example.alphabettracer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.AchievementStorage
import com.example.alphabettracer.data.LetterStorage
import com.example.alphabettracer.data.alphabetList
import com.example.alphabettracer.model.Achievement
import com.example.alphabettracer.model.ScreenState
import com.example.alphabettracer.model.WordSearchTopic
import com.example.alphabettracer.ui.components.AchievementPopup
import com.example.alphabettracer.util.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTracingApp() {
    val context = LocalContext.current
    var screenState by remember { mutableStateOf(ScreenState.LETTER_GRID) }
    var currentIndex by remember { mutableStateOf(0) }
    var userStreak by remember { mutableStateOf(0) }
    var selectedTopic by remember { mutableStateOf<WordSearchTopic?>(null) }

    // Load saved results from storage
    var letterResults by remember {
        mutableStateOf(LetterStorage.getAllResults(context, alphabetList.size))
    }
    var totalStars by remember {
        mutableStateOf(LetterStorage.getTotalStars(context, alphabetList.size))
    }

    // Achievement tracking
    var unlockedAchievements by remember {
        mutableStateOf(AchievementStorage.getUnlockedAchievements(context))
    }
    var pendingAchievement by remember { mutableStateOf<Achievement?>(null) }
    var colorsUsed by remember {
        mutableStateOf(AchievementStorage.getColorsUsedCount(context))
    }

    // Initialize SoundManager on first composition
    LaunchedEffect(Unit) {
        SoundManager.initialize(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Haate Khori",
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(12.dp))
                        // Stars counter
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 16.sp)
                                Spacer(Modifier.width(4.dp))
                                Text("$totalStars", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                },
                navigationIcon = {
                    when (screenState) {
                        ScreenState.TRACING -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to grid")
                            }
                        }
                        ScreenState.WORD_SEARCH_TOPICS -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to home")
                            }
                        }
                        ScreenState.WORD_SEARCH_GAME -> {
                            IconButton(onClick = { screenState = ScreenState.WORD_SEARCH_TOPICS }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to topics")
                            }
                        }
                        ScreenState.STICK_BUILDER -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to home")
                            }
                        }
                        ScreenState.COUNTING_GAME -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to home")
                            }
                        }
                        ScreenState.MEMORY_MATCH -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to home")
                            }
                        }
                        ScreenState.PATTERN_GAME -> {
                            IconButton(onClick = { screenState = ScreenState.LETTER_GRID }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back to home")
                            }
                        }
                        else -> {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF6200EE),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8E1),  // Warm cream at top
                            Color(0xFFE3F2FD),  // Light blue
                            Color(0xFFF3E5F5)   // Light purple at bottom
                        )
                    )
                )
        ) {
            when (screenState) {
                ScreenState.LETTER_GRID -> {
                    LetterGridScreen(
                        letterResults = letterResults,
                        unlockedAchievements = unlockedAchievements,
                        onLetterSelected = { index ->
                            currentIndex = index
                            screenState = ScreenState.TRACING
                        },
                        onWordSearchClicked = {
                            screenState = ScreenState.WORD_SEARCH_TOPICS
                        },
                        onStickBuilderClicked = {
                            screenState = ScreenState.STICK_BUILDER
                        },
                        onCountingGameClicked = {
                            screenState = ScreenState.COUNTING_GAME
                        },
                        onMemoryMatchClicked = {
                            screenState = ScreenState.MEMORY_MATCH
                        },
                        onPatternGameClicked = {
                            screenState = ScreenState.PATTERN_GAME
                        }
                    )
                }
                ScreenState.TRACING -> {
                    TracingScreen(
                        currentIndex = currentIndex,
                        userStreak = userStreak,
                        onStreakUpdate = { newStreak ->
                            userStreak = newStreak
                            AchievementStorage.updateMaxStreak(context, newStreak)
                        },
                        onResultSaved = { index, result ->
                            // Save to persistent storage
                            LetterStorage.saveLetterResult(context, index, result)
                            // Update local state
                            letterResults = LetterStorage.getAllResults(context, alphabetList.size)
                            totalStars = LetterStorage.getTotalStars(context, alphabetList.size)

                            // Check for new achievements
                            val newAchievements = AchievementStorage.checkAndUnlockAchievements(
                                context = context,
                                totalStars = totalStars,
                                currentStreak = userStreak,
                                colorsUsed = colorsUsed
                            )
                            if (newAchievements.isNotEmpty()) {
                                unlockedAchievements = AchievementStorage.getUnlockedAchievements(context)
                                pendingAchievement = newAchievements.first()
                            }
                        },
                        onColorUsed = { colorIndex ->
                            AchievementStorage.recordColorUsed(context, colorIndex)
                            colorsUsed = AchievementStorage.getColorsUsedCount(context)
                        },
                        onNavigate = { newIndex ->
                            currentIndex = newIndex
                        }
                    )
                }
                ScreenState.WORD_SEARCH_TOPICS -> {
                    WordSearchTopicScreen(
                        onTopicSelected = { topic ->
                            selectedTopic = topic
                            screenState = ScreenState.WORD_SEARCH_GAME
                        },
                        onBackPressed = {
                            screenState = ScreenState.LETTER_GRID
                        }
                    )
                }
                ScreenState.WORD_SEARCH_GAME -> {
                    selectedTopic?.let { topic ->
                        WordSearchGameScreen(
                            topic = topic,
                            onBackPressed = {
                                screenState = ScreenState.WORD_SEARCH_TOPICS
                            },
                            onGameComplete = {
                                // Game completed
                            }
                        )
                    }
                }
                ScreenState.STICK_BUILDER -> {
                    StickBuilderScreen(
                        onBackPressed = {
                            screenState = ScreenState.LETTER_GRID
                        }
                    )
                }
                ScreenState.COUNTING_GAME -> {
                    CountingGameScreen(
                        onBackPressed = {
                            screenState = ScreenState.LETTER_GRID
                        }
                    )
                }
                ScreenState.MEMORY_MATCH -> {
                    MemoryMatchScreen(
                        onBackPressed = {
                            screenState = ScreenState.LETTER_GRID
                        }
                    )
                }
                ScreenState.PATTERN_GAME -> {
                    PatternGameScreen(
                        onBackPressed = {
                            screenState = ScreenState.LETTER_GRID
                        }
                    )
                }
            }
        }
    }

    // Achievement popup
    pendingAchievement?.let { achievement ->
        // Play achievement sound when popup appears
        LaunchedEffect(achievement) {
            SoundManager.playAchievement(context)
        }
        AchievementPopup(
            achievement = achievement,
            isVisible = true,
            onDismiss = { pendingAchievement = null }
        )
    }
}