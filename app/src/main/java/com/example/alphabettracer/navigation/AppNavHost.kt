package com.example.alphabettracer.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.alphabettracer.constants.AppDimensions
import com.example.alphabettracer.model.Achievement
import com.example.alphabettracer.model.MatchResult
import com.example.alphabettracer.model.WordSearchTopic
import com.example.alphabettracer.ui.screens.CountingGameScreen
import com.example.alphabettracer.ui.screens.LetterGridScreen
import com.example.alphabettracer.ui.screens.LetterSelectionScreen
import com.example.alphabettracer.ui.screens.MemoryMatchScreen
import com.example.alphabettracer.ui.screens.PatternGameScreen
import com.example.alphabettracer.ui.screens.StickBuilderScreen
import com.example.alphabettracer.ui.screens.TracingScreen
import com.example.alphabettracer.ui.screens.WordSearchGameScreen
import com.example.alphabettracer.ui.screens.WordSearchTopicScreen

/**
 * Main navigation host for the app.
 * Sets up all navigation routes and transitions.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    letterResults: Map<Int, MatchResult>,
    unlockedAchievements: Set<Achievement>,
    userStreak: Int,
    totalStars: Int,
    selectedTopic: WordSearchTopic?,
    onLetterSelected: (Int) -> Unit,
    onTopicSelected: (WordSearchTopic) -> Unit,
    onStreakUpdate: (Int) -> Unit,
    onResultSaved: (Int, MatchResult) -> Unit,
    onColorUsed: (Int) -> Unit,
    onNavigateLetter: (Int) -> Unit,
    currentLetterIndex: Int
) {
    val animationDuration = AppDimensions.AnimationDuration.Medium

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(animationDuration)) +
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(animationDuration)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(animationDuration)) +
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(animationDuration)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(animationDuration)) +
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(animationDuration)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(animationDuration)) +
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(animationDuration)
            )
        }
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            LetterGridScreen(
                letterResults = letterResults,
                onLetterPracticeClicked = {
                    navController.navigateToLetterSelection()
                },
                onWordSearchClicked = {
                    navController.navigateToWordSearchTopics()
                },
                onStickBuilderClicked = {
                    navController.navigateToStickBuilder()
                },
                onCountingGameClicked = {
                    navController.navigateToCountingGame()
                },
                onMemoryMatchClicked = {
                    navController.navigateToMemoryMatch()
                },
                onPatternGameClicked = {
                    navController.navigateToPatternGame()
                }
            )
        }

        // Letter Selection Screen
        composable(Screen.LetterSelection.route) {
            LetterSelectionScreen(
                letterResults = letterResults,
                unlockedAchievements = unlockedAchievements,
                onLetterSelected = { index ->
                    onLetterSelected(index)
                    navController.navigateToTracing(index)
                }
            )
        }

        // Tracing Screen
        composable(
            route = Screen.Tracing.route,
            arguments = listOf(
                navArgument(NavArgs.LETTER_INDEX) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val letterIndex = backStackEntry.arguments?.getInt(NavArgs.LETTER_INDEX) ?: 0
            TracingScreen(
                currentIndex = letterIndex,
                userStreak = userStreak,
                onStreakUpdate = onStreakUpdate,
                onResultSaved = onResultSaved,
                onColorUsed = onColorUsed,
                onNavigate = { newIndex ->
                    onNavigateLetter(newIndex)
                    // Navigate to new letter
                    navController.navigate(Screen.Tracing.createRoute(newIndex)) {
                        popUpTo(Screen.Tracing.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        // Word Search Topics
        composable(Screen.WordSearchTopics.route) {
            WordSearchTopicScreen(
                onTopicSelected = { topic ->
                    onTopicSelected(topic)
                    navController.navigateToWordSearchGame(topic.id)
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // Word Search Game
        composable(
            route = Screen.WordSearchGame.route,
            arguments = listOf(
                navArgument(NavArgs.TOPIC_ID) {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            selectedTopic?.let { topic ->
                WordSearchGameScreen(
                    topic = topic,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onGameComplete = {
                        // Game completed - could add analytics here
                    }
                )
            }
        }

        // Stick Builder
        composable(Screen.StickBuilder.route) {
            StickBuilderScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // Counting Game
        composable(Screen.CountingGame.route) {
            CountingGameScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // Memory Match
        composable(Screen.MemoryMatch.route) {
            MemoryMatchScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // Pattern Game
        composable(Screen.PatternGame.route) {
            PatternGameScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}
