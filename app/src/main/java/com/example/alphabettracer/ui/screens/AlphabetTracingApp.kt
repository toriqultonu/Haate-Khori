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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.alphabettracer.constants.AppColors
import com.example.alphabettracer.constants.AppDimensions
import com.example.alphabettracer.constants.AppEmojis
import com.example.alphabettracer.constants.AppStrings
import com.example.alphabettracer.content.AlphabetContent
import com.example.alphabettracer.data.AchievementStorage
import com.example.alphabettracer.data.LetterStorage
import com.example.alphabettracer.model.Achievement
import com.example.alphabettracer.model.WordSearchTopic
import com.example.alphabettracer.navigation.AppNavHost
import com.example.alphabettracer.navigation.getScreenTitle
import com.example.alphabettracer.navigation.shouldShowBackButton
import com.example.alphabettracer.ui.components.AchievementPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetTracingApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var currentIndex by remember { mutableIntStateOf(0) }
    var userStreak by remember { mutableIntStateOf(0) }
    var selectedTopic by remember { mutableStateOf<WordSearchTopic?>(null) }

    // Load saved results from storage
    var letterResults by remember {
        mutableStateOf(LetterStorage.getAllResults(context, AlphabetContent.alphabetList.size))
    }
    var totalStars by remember {
        mutableStateOf(LetterStorage.getTotalStars(context, AlphabetContent.alphabetList.size))
    }

    // Achievement tracking
    var unlockedAchievements by remember {
        mutableStateOf(AchievementStorage.getUnlockedAchievements(context))
    }
    var pendingAchievement by remember { mutableStateOf<Achievement?>(null) }
    var colorsUsed by remember {
        mutableStateOf(AchievementStorage.getColorsUsedCount(context))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            getScreenTitle(currentRoute),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(AppDimensions.Spacing.Medium))
                        // Stars counter
                        Surface(
                            color = AppColors.Achievement.Gold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(AppDimensions.CornerRadius.Default)
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = AppDimensions.Padding.Medium,
                                    vertical = AppDimensions.Padding.ExtraSmall
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(AppEmojis.STAR, fontSize = AppDimensions.TextSize.Default)
                                Spacer(Modifier.width(AppDimensions.Spacing.ExtraSmall))
                                Text(
                                    "$totalStars",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = AppDimensions.TextSize.Medium
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    if (shouldShowBackButton(currentRoute)) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = AppStrings.Navigation.BACK
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.TopBarBackground,
                    titleContentColor = AppColors.TopBarContent,
                    navigationIconContentColor = AppColors.TopBarContent
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
                            AppColors.Gradients.BackgroundTop,
                            AppColors.Gradients.BackgroundMiddle,
                            AppColors.Gradients.BackgroundBottom
                        )
                    )
                )
        ) {
            AppNavHost(
                navController = navController,
                letterResults = letterResults,
                unlockedAchievements = unlockedAchievements,
                userStreak = userStreak,
                totalStars = totalStars,
                selectedTopic = selectedTopic,
                currentLetterIndex = currentIndex,
                onLetterSelected = { index ->
                    currentIndex = index
                },
                onTopicSelected = { topic ->
                    selectedTopic = topic
                },
                onStreakUpdate = { newStreak ->
                    userStreak = newStreak
                    AchievementStorage.updateMaxStreak(context, newStreak)
                },
                onResultSaved = { index, result ->
                    // Save to persistent storage
                    LetterStorage.saveLetterResult(context, index, result)
                    // Update local state
                    letterResults = LetterStorage.getAllResults(context, AlphabetContent.alphabetList.size)
                    totalStars = LetterStorage.getTotalStars(context, AlphabetContent.alphabetList.size)

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
                onNavigateLetter = { newIndex ->
                    currentIndex = newIndex
                }
            )
        }
    }

    // Achievement popup
    pendingAchievement?.let { achievement ->
        AchievementPopup(
            achievement = achievement,
            isVisible = true,
            onDismiss = { pendingAchievement = null }
        )
    }
}
