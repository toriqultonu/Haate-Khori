package com.example.alphabettracer.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.MemoryCard
import com.example.alphabettracer.data.MemoryCategory
import com.example.alphabettracer.data.MemoryDifficulty
import com.example.alphabettracer.data.MemoryMatchStorage
import com.example.alphabettracer.data.calculateMemoryStars
import com.example.alphabettracer.data.generateMemoryDeck
import com.example.alphabettracer.data.memoryCategories
import com.example.alphabettracer.ui.components.ConfettiAnimation
import com.example.alphabettracer.ui.components.MemoryMatchTutorial
import kotlinx.coroutines.delay

@Composable
fun MemoryMatchScreen(
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current

    // Game setup state
    var selectedCategory by remember { mutableStateOf<MemoryCategory?>(null) }
    var selectedDifficulty by remember { mutableStateOf<MemoryDifficulty?>(null) }

    // Game state
    val cards = remember { mutableStateListOf<MemoryCard>() }
    var moves by remember { mutableIntStateOf(0) }
    var matchedPairs by remember { mutableIntStateOf(0) }
    var firstFlippedIndex by remember { mutableStateOf<Int?>(null) }
    var isChecking by remember { mutableStateOf(false) }
    var gameComplete by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var gameStarted by remember { mutableStateOf(false) }
    var showTutorial by remember { mutableStateOf(false) }

    // Check if tutorial should be shown on first launch
    LaunchedEffect(Unit) {
        if (!MemoryMatchStorage.hasShownTutorial(context)) {
            showTutorial = true
        }
    }

    // Timer
    LaunchedEffect(gameStarted, gameComplete) {
        if (gameStarted && !gameComplete) {
            while (true) {
                delay(1000)
                elapsedSeconds++
            }
        }
    }

    // Start game function
    fun startGame(category: MemoryCategory, difficulty: MemoryDifficulty) {
        cards.clear()
        cards.addAll(generateMemoryDeck(category, difficulty))
        moves = 0
        matchedPairs = 0
        firstFlippedIndex = null
        isChecking = false
        gameComplete = false
        elapsedSeconds = 0
        gameStarted = true
        selectedCategory = category
        selectedDifficulty = difficulty
    }

    // Handle card tap
    fun onCardTap(index: Int) {
        if (isChecking || cards[index].isFlipped || cards[index].isMatched) return

        cards[index] = cards[index].copy(isFlipped = true)

        if (firstFlippedIndex == null) {
            firstFlippedIndex = index
        } else {
            moves++
            isChecking = true
            val firstIndex = firstFlippedIndex!!
            val first = cards[firstIndex]
            val second = cards[index]

            if (first.pairId == second.pairId) {
                // Match found
                cards[firstIndex] = first.copy(isMatched = true)
                cards[index] = second.copy(isMatched = true)
                matchedPairs++

                if (matchedPairs == selectedDifficulty!!.pairCount) {
                    gameComplete = true
                    showConfetti = true
                    MemoryMatchStorage.incrementGamesWon(context)
                    MemoryMatchStorage.incrementTotalGames(context)
                    MemoryMatchStorage.saveBestMoves(context, selectedDifficulty!!.name, moves)
                    MemoryMatchStorage.saveBestTime(context, selectedDifficulty!!.name, elapsedSeconds)
                    MemoryMatchStorage.addMatches(context, matchedPairs)
                }

                firstFlippedIndex = null
                isChecking = false
            }
        }
    }

    // Flip cards back if no match
    LaunchedEffect(isChecking, firstFlippedIndex) {
        if (isChecking && firstFlippedIndex != null) {
            val flippedCards = cards.mapIndexedNotNull { idx, card ->
                if (card.isFlipped && !card.isMatched) idx else null
            }
            if (flippedCards.size == 2) {
                delay(1000)
                flippedCards.forEach { idx ->
                    cards[idx] = cards[idx].copy(isFlipped = false)
                }
                firstFlippedIndex = null
                isChecking = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedCategory == null || selectedDifficulty == null) {
            // Category and difficulty selection
            CategorySelectionScreen(
                onStartGame = { category, difficulty -> startGame(category, difficulty) },
                onBackPressed = onBackPressed
            )
        } else if (gameComplete) {
            // Game complete screen
            val stars = calculateMemoryStars(
                moves = moves,
                optimalMoves = selectedDifficulty!!.pairCount,
                timeSeconds = elapsedSeconds
            )
            GameCompleteScreen(
                moves = moves,
                timeSeconds = elapsedSeconds,
                stars = stars,
                onPlayAgain = { startGame(selectedCategory!!, selectedDifficulty!!) },
                onChangeSettings = {
                    selectedCategory = null
                    selectedDifficulty = null
                    gameStarted = false
                },
                onBackPressed = onBackPressed
            )
        } else {
            // Game board
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🧠", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Memory Match",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6200EE)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Moves", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "$moves",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Time", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    formatTime(elapsedSeconds),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Pairs", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "$matchedPairs/${selectedDifficulty!!.pairCount}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Card grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(selectedDifficulty!!.gridColumns),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(cards) { index, card ->
                        MemoryCardView(
                            card = card,
                            onClick = { onCardTap(index) },
                            categoryColor = Color(selectedCategory!!.color)
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

        // Tutorial overlay for first-time users
        MemoryMatchTutorial(
            isVisible = showTutorial,
            onDismiss = {
                showTutorial = false
                MemoryMatchStorage.markTutorialShown(context)
            },
            onSkip = {
                showTutorial = false
                MemoryMatchStorage.markTutorialShown(context)
            }
        )
    }
}

@Composable
private fun MemoryCardView(
    card: MemoryCard,
    onClick: () -> Unit,
    categoryColor: Color
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isFlipped || card.isMatched) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "rotation"
    )

    val scale by animateFloatAsState(
        targetValue = if (card.isMatched) 0.9f else 1f,
        animationSpec = spring(),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (rotation > 90f) {
                if (card.isMatched) categoryColor.copy(alpha = 0.3f) else Color.White
            } else {
                categoryColor
            }
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (card.isMatched) 0.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation > 90f) {
                Text(
                    text = card.content,
                    fontSize = 36.sp,
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                )
            } else {
                Text("❓", fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun CategorySelectionScreen(
    onStartGame: (MemoryCategory, MemoryDifficulty) -> Unit,
    onBackPressed: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<MemoryCategory?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🧠", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "Memory Match",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                    Text(
                        "Find matching pairs!",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (selectedCategory == null) {
            Text(
                "Choose a Category",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(memoryCategories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        } else {
            Text(
                "Choose Difficulty",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Category: ${selectedCategory!!.emoji} ${selectedCategory!!.name}",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            MemoryDifficulty.entries.forEach { difficulty ->
                Button(
                    onClick = { onStartGame(selectedCategory!!, difficulty) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(selectedCategory!!.color)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        difficulty.label,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { selectedCategory = null },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back to Categories")
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onBackPressed,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
private fun CategoryCard(
    category: MemoryCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(category.color)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(category.emoji, fontSize = 48.sp)
            Spacer(Modifier.height(8.dp))
            Text(
                category.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun GameCompleteScreen(
    moves: Int,
    timeSeconds: Int,
    stars: Int,
    onPlayAgain: () -> Unit,
    onChangeSettings: () -> Unit,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🎉 Congratulations! 🎉",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(24.dp))

                // Stars display
                Row {
                    repeat(3) { index ->
                        Text(
                            if (index < stars) "⭐" else "☆",
                            fontSize = 48.sp
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = Color(0xFF2196F3).copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "$moves",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Moves", fontSize = 14.sp, color = Color.Gray)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = Color(0xFFFF9800).copy(alpha = 0.1f),
                            shape = CircleShape,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    formatTime(timeSeconds),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF9800)
                                )
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Time", fontSize = 14.sp, color = Color.Gray)
                    }
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Play Again", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onChangeSettings,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Change Category", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = onBackPressed,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) "${mins}:${secs.toString().padStart(2, '0')}" else "${secs}s"
}
