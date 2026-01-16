package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.CountingDifficulty
import com.example.alphabettracer.data.CountingGameStorage
import com.example.alphabettracer.data.generateAnswerOptions
import com.example.alphabettracer.data.generateCountingQuestion
import com.example.alphabettracer.ui.components.ConfettiAnimation
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingGameScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Game state
    var difficulty by remember { mutableStateOf(CountingDifficulty.EASY) }
    var currentQuestion by remember { mutableStateOf(generateCountingQuestion(difficulty.minCount, difficulty.maxCount)) }
    var answerOptions by remember { mutableStateOf(generateAnswerOptions(currentQuestion.second)) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var questionsAnswered by remember { mutableIntStateOf(0) }
    var showResult by remember { mutableStateOf<Boolean?>(null) }
    var showConfetti by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }

    val totalQuestions = 10
    val highScore = CountingGameStorage.getHighScore(context)

    // Generate new question
    fun nextQuestion() {
        currentQuestion = generateCountingQuestion(difficulty.minCount, difficulty.maxCount)
        answerOptions = generateAnswerOptions(currentQuestion.second)
        showResult = null
    }

    // Handle answer selection
    fun onAnswerSelected(answer: Int) {
        if (showResult != null) return // Prevent double-tap

        val correct = answer == currentQuestion.second
        showResult = correct
        questionsAnswered++

        if (correct) {
            score++
            streak++
            CountingGameStorage.incrementTotalCorrect(context)
            CountingGameStorage.saveBestStreak(context, streak)
            if (streak >= 3) {
                showConfetti = true
            }
        } else {
            streak = 0
        }

        if (questionsAnswered >= totalQuestions) {
            gameComplete = true
            CountingGameStorage.saveHighScore(context, score)
            CountingGameStorage.incrementGamesPlayed(context)
        }
    }

    // Auto-advance after showing result
    LaunchedEffect(showResult) {
        if (showResult != null && !gameComplete) {
            delay(1500)
            nextQuestion()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with score and streak
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔢", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Count & Learn!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EE)
                            )
                        }
                        Surface(
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⭐", fontSize = 16.sp)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "$score/$totalQuestions",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { questionsAnswered.toFloat() / totalQuestions },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = Color(0xFF6200EE),
                        trackColor = Color(0xFFE8E8E8)
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Question ${questionsAnswered + 1}/$totalQuestions",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        if (streak > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥", fontSize = 14.sp)
                                Text(
                                    " Streak: $streak",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            if (gameComplete) {
                // Game complete screen
                GameCompleteCard(
                    score = score,
                    totalQuestions = totalQuestions,
                    highScore = highScore,
                    isNewHighScore = score > highScore,
                    onPlayAgain = {
                        score = 0
                        streak = 0
                        questionsAnswered = 0
                        gameComplete = false
                        nextQuestion()
                    },
                    onBack = onBackPressed
                )
            } else {
                // Objects to count
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "How many do you see?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF333333)
                        )

                        Spacer(Modifier.height(24.dp))

                        // Display emojis
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 5
                        ) {
                            currentQuestion.first.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 48.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                        // Show result feedback
                        AnimatedVisibility(
                            visible = showResult != null,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Surface(
                                color = if (showResult == true) Color(0xFF4CAF50).copy(alpha = 0.2f)
                                else Color(0xFFF44336).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = if (showResult == true) "Correct! The answer is ${currentQuestion.second}"
                                    else "Oops! The answer was ${currentQuestion.second}",
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showResult == true) Color(0xFF4CAF50) else Color(0xFFF44336)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Answer options
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(answerOptions) { option ->
                        AnswerButton(
                            number = option,
                            isCorrect = if (showResult != null) option == currentQuestion.second else null,
                            isSelected = showResult != null,
                            onClick = { onAnswerSelected(option) },
                            enabled = showResult == null
                        )
                    }
                }
            }
        }

        // Confetti overlay
        ConfettiAnimation(
            isPlaying = showConfetti,
            onAnimationEnd = { showConfetti = false },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun AnswerButton(
    number: Int,
    isCorrect: Boolean?,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    val backgroundColor = when {
        isCorrect == true -> Color(0xFF4CAF50)
        isCorrect == false && isSelected -> Color(0xFFF44336).copy(alpha = 0.3f)
        else -> Color(0xFF6200EE)
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .height(80.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource
    ) {
        Text(
            text = number.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameCompleteCard(
    score: Int,
    totalQuestions: Int,
    highScore: Int,
    isNewHighScore: Boolean,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isNewHighScore) "🎉 New High Score! 🎉" else "🎊 Game Complete! 🎊",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF6200EE),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // Score display
            Surface(
                color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                shape = CircleShape,
                modifier = Modifier.size(120.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$score",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                        Text(
                            text = "out of $totalQuestions",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Performance message
            Text(
                text = when {
                    score == totalQuestions -> "Perfect! You're a counting champion!"
                    score >= totalQuestions * 0.8 -> "Great job! Almost perfect!"
                    score >= totalQuestions * 0.6 -> "Good work! Keep practicing!"
                    else -> "Nice try! Practice makes perfect!"
                },
                fontSize = 16.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            if (!isNewHighScore && highScore > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "High Score: $highScore",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back")
                }
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Play Again")
                }
            }
        }
    }
}
