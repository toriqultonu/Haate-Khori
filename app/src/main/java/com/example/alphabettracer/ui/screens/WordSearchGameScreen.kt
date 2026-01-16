package com.example.alphabettracer.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.data.WordSearchStorage
import com.example.alphabettracer.data.wordHighlightColors
import com.example.alphabettracer.model.CellSelection
import com.example.alphabettracer.model.FoundWord
import com.example.alphabettracer.model.PlacedWord
import com.example.alphabettracer.model.WordDirection
import com.example.alphabettracer.model.WordSearchTopic
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WordSearchGameScreen(
    topic: WordSearchTopic,
    onBackPressed: () -> Unit,
    onGameComplete: () -> Unit
) {
    val context = LocalContext.current
    val gridSize = 10

    // Game state
    var grid by remember { mutableStateOf(Array(gridSize) { CharArray(gridSize) { ' ' } }) }
    var placedWords by remember { mutableStateOf(listOf<PlacedWord>()) }
    val foundWords = remember { mutableStateListOf<FoundWord>() }
    var currentSelection by remember { mutableStateOf(listOf<CellSelection>()) }
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var gameCompleted by remember { mutableStateOf(false) }
    var colorIndex by remember { mutableIntStateOf(0) }

    // Initialize grid - load saved game or create new
    LaunchedEffect(topic) {
        val savedState = WordSearchStorage.loadGameState(context, topic.id)
        if (savedState != null) {
            // Restore saved game
            grid = savedState.grid
            placedWords = savedState.placedWords
            elapsedSeconds = savedState.elapsedSeconds
            colorIndex = savedState.colorIndex

            // Restore found words with their colors
            foundWords.clear()
            savedState.foundWordNames.forEachIndexed { index, wordName ->
                val placed = savedState.placedWords.find { it.word == wordName }
                if (placed != null) {
                    val direction = placed.direction
                    val (rowDelta, colDelta) = when (direction) {
                        WordDirection.HORIZONTAL -> Pair(0, 1)
                        WordDirection.VERTICAL -> Pair(1, 0)
                        WordDirection.DIAGONAL_DOWN -> Pair(1, 1)
                        WordDirection.DIAGONAL_UP -> Pair(-1, 1)
                    }
                    val endRow = placed.startRow + (placed.word.length - 1) * rowDelta
                    val endCol = placed.startCol + (placed.word.length - 1) * colDelta

                    foundWords.add(
                        FoundWord(
                            word = wordName,
                            startRow = placed.startRow,
                            startCol = placed.startCol,
                            endRow = endRow,
                            endCol = endCol,
                            color = wordHighlightColors[index % wordHighlightColors.size]
                        )
                    )
                }
            }
        } else {
            // Create new game
            val result = generateWordSearchGrid(topic.words, gridSize)
            grid = result.first
            placedWords = result.second
        }
    }

    // Save game state when leaving (if not completed)
    val currentGrid by rememberUpdatedState(grid)
    val currentPlacedWords by rememberUpdatedState(placedWords)
    val currentFoundWords by rememberUpdatedState(foundWords.toList())
    val currentElapsedSeconds by rememberUpdatedState(elapsedSeconds)
    val currentColorIndex by rememberUpdatedState(colorIndex)
    val currentGameCompleted by rememberUpdatedState(gameCompleted)

    DisposableEffect(topic.id) {
        onDispose {
            // Save game state if not completed and has progress
            if (!currentGameCompleted && currentPlacedWords.isNotEmpty()) {
                WordSearchStorage.saveGameState(
                    context = context,
                    topicId = topic.id,
                    grid = currentGrid,
                    placedWords = currentPlacedWords,
                    foundWordNames = currentFoundWords.map { it.word },
                    elapsedSeconds = currentElapsedSeconds,
                    colorIndex = currentColorIndex
                )
            }
        }
    }

    // Timer
    LaunchedEffect(gameCompleted) {
        while (!gameCompleted) {
            delay(1000)
            elapsedSeconds++
        }
    }

    // Check game completion
    LaunchedEffect(foundWords.size, placedWords.size) {
        if (placedWords.isNotEmpty() && foundWords.size == placedWords.size && !gameCompleted) {
            gameCompleted = true
            WordSearchStorage.saveTopicCompleted(context, topic.id, elapsedSeconds.toLong())
            // Clear saved game since it's completed
            WordSearchStorage.clearSavedGame(context, topic.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF8E1), Color(0xFFE3F2FD))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with topic name and timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = topic.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = topic.color
                )
                Text(
                    text = "Find all ${placedWords.size} words!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Timer
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⏱️", fontSize = 18.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatTime(elapsedSeconds),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Progress
        Text(
            text = "Found: ${foundWords.size}/${placedWords.size}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4CAF50)
        )

        Spacer(Modifier.height(12.dp))

        // Word List
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                placedWords.forEach { placed ->
                    val found = foundWords.any { it.word == placed.word }
                    val foundWord = foundWords.find { it.word == placed.word }
                    WordChip(
                        word = placed.word,
                        isFound = found,
                        highlightColor = foundWord?.color
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Game Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(8.dp)
            ) {
                WordSearchGrid(
                    grid = grid,
                    gridSize = gridSize,
                    foundWords = foundWords,
                    currentSelection = currentSelection,
                    onSelectionChange = { selection ->
                        currentSelection = selection
                    },
                    onSelectionComplete = { selection ->
                        // Check if selection forms a valid word
                        val selectedWord = selection.map { grid[it.row][it.col] }.joinToString("")
                        val reversedWord = selectedWord.reversed()

                        val matchedPlaced = placedWords.find { placed ->
                            (placed.word == selectedWord || placed.word == reversedWord) &&
                            foundWords.none { it.word == placed.word }
                        }

                        if (matchedPlaced != null && selection.isNotEmpty()) {
                            val highlightColor = wordHighlightColors[colorIndex % wordHighlightColors.size]
                            colorIndex++

                            foundWords.add(
                                FoundWord(
                                    word = matchedPlaced.word,
                                    startRow = selection.first().row,
                                    startCol = selection.first().col,
                                    endRow = selection.last().row,
                                    endCol = selection.last().col,
                                    color = highlightColor
                                )
                            )
                        }
                        currentSelection = emptyList()
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // New Game Button
            Button(
                onClick = {
                    // Clear any saved game state
                    WordSearchStorage.clearSavedGame(context, topic.id)

                    // Generate fresh game
                    val result = generateWordSearchGrid(topic.words, gridSize)
                    grid = result.first
                    placedWords = result.second
                    foundWords.clear()
                    elapsedSeconds = 0
                    gameCompleted = false
                    colorIndex = 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "New Game")
                Spacer(Modifier.width(4.dp))
                Text("New Game")
            }

            // Back Button
            Button(
                onClick = onBackPressed,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Back to Topics")
            }
        }

        // Completion message
        if (gameCompleted) {
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🎉", fontSize = 48.sp)
                    Text(
                        "Congratulations!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "You found all words in ${formatTime(elapsedSeconds)}!",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun WordChip(
    word: String,
    isFound: Boolean,
    highlightColor: Color?
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isFound && highlightColor != null -> highlightColor.copy(alpha = 0.3f)
            isFound -> Color(0xFF4CAF50).copy(alpha = 0.2f)
            else -> Color(0xFFF5F5F5)
        },
        label = "chipBg"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFound) 1.05f else 1f,
        animationSpec = spring(),
        label = "chipScale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = word,
                fontSize = 14.sp,
                fontWeight = if (isFound) FontWeight.Bold else FontWeight.Normal,
                color = if (isFound) Color(0xFF2E7D32) else Color.DarkGray,
                textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
            )
            if (isFound) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Found",
                    modifier = Modifier.size(16.dp),
                    tint = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun WordSearchGrid(
    grid: Array<CharArray>,
    gridSize: Int,
    foundWords: List<FoundWord>,
    currentSelection: List<CellSelection>,
    onSelectionChange: (List<CellSelection>) -> Unit,
    onSelectionComplete: (List<CellSelection>) -> Unit
) {
    // Track selection internally during drag
    var internalSelection by remember { mutableStateOf(listOf<CellSelection>()) }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(gridSize) {
                val cellSizeInput = size.width.toFloat() / gridSize

                awaitEachGesture {
                    // Wait for first touch down and consume it immediately
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()

                    // Calculate starting cell
                    val startCol = (down.position.x / cellSizeInput).toInt().coerceIn(0, gridSize - 1)
                    val startRow = (down.position.y / cellSizeInput).toInt().coerceIn(0, gridSize - 1)
                    val startCell = CellSelection(startRow, startCol)

                    // Set initial selection
                    internalSelection = listOf(startCell)
                    onSelectionChange(internalSelection)

                    // Track drag - consume all pointer events to prevent scroll from intercepting
                    var lastPosition = down.position
                    do {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                change.consume()
                                lastPosition = change.position

                                val col = (lastPosition.x / cellSizeInput).toInt().coerceIn(0, gridSize - 1)
                                val row = (lastPosition.y / cellSizeInput).toInt().coerceIn(0, gridSize - 1)
                                val currentCell = CellSelection(row, col)

                                // Only update if it's a valid line (horizontal, vertical, or diagonal)
                                if (isValidSelection(startCell, currentCell)) {
                                    val cells = getCellsBetween(startCell, currentCell)
                                    internalSelection = cells
                                    onSelectionChange(cells)
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    // Drag ended - complete selection
                    onSelectionComplete(internalSelection)
                    internalSelection = emptyList()
                }
            }
    ) {
        val cellSize = size.width / gridSize

        // Draw found word highlights
        foundWords.forEach { found ->
            val startX = found.startCol * cellSize + cellSize / 2
            val startY = found.startRow * cellSize + cellSize / 2
            val endX = found.endCol * cellSize + cellSize / 2
            val endY = found.endRow * cellSize + cellSize / 2

            drawLine(
                color = found.color.copy(alpha = 0.6f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = cellSize * 0.8f,
                cap = StrokeCap.Round
            )
        }

        // Draw current selection (use internal selection for active drag, otherwise use passed selection)
        val selectionToDraw = if (internalSelection.isNotEmpty()) internalSelection else currentSelection
        if (selectionToDraw.isNotEmpty()) {
            val first = selectionToDraw.first()
            val last = selectionToDraw.last()

            val startX = first.col * cellSize + cellSize / 2
            val startY = first.row * cellSize + cellSize / 2
            val endX = last.col * cellSize + cellSize / 2
            val endY = last.row * cellSize + cellSize / 2

            drawLine(
                color = Color(0xFF6200EE).copy(alpha = 0.4f),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = cellSize * 0.8f,
                cap = StrokeCap.Round
            )
        }

        // Draw grid lines
        for (i in 0..gridSize) {
            val pos = i * cellSize
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(pos, 0f),
                end = Offset(pos, size.height),
                strokeWidth = 1f
            )
            drawLine(
                color = Color.LightGray.copy(alpha = 0.3f),
                start = Offset(0f, pos),
                end = Offset(size.width, pos),
                strokeWidth = 1f
            )
        }

        // Draw letters
        val activeSelection = if (internalSelection.isNotEmpty()) internalSelection else currentSelection
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val letter = grid[row][col]
                val x = col * cellSize + cellSize / 2
                val y = row * cellSize + cellSize / 2

                val isSelected = activeSelection.any { it.row == row && it.col == col }
                val isInFoundWord = foundWords.any { found ->
                    val cells = getCellsBetween(
                        CellSelection(found.startRow, found.startCol),
                        CellSelection(found.endRow, found.endCol)
                    )
                    cells.any { it.row == row && it.col == col }
                }

                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = when {
                            isSelected -> android.graphics.Color.parseColor("#6200EE")
                            isInFoundWord -> android.graphics.Color.parseColor("#1B5E20")
                            else -> android.graphics.Color.parseColor("#333333")
                        }
                        textSize = cellSize * 0.6f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isFakeBoldText = isSelected || isInFoundWord
                        isAntiAlias = true
                    }
                    drawText(
                        letter.toString(),
                        x,
                        y + (paint.textSize / 3),
                        paint
                    )
                }
            }
        }
    }
}

private fun isValidSelection(first: CellSelection, current: CellSelection): Boolean {
    val rowDiff = current.row - first.row
    val colDiff = current.col - first.col

    // Same cell
    if (rowDiff == 0 && colDiff == 0) return true

    // Horizontal
    if (rowDiff == 0) return true

    // Vertical
    if (colDiff == 0) return true

    // Diagonal
    if (kotlin.math.abs(rowDiff) == kotlin.math.abs(colDiff)) return true

    return false
}

private fun getCellsBetween(start: CellSelection, end: CellSelection): List<CellSelection> {
    val cells = mutableListOf<CellSelection>()

    val rowStep = when {
        end.row > start.row -> 1
        end.row < start.row -> -1
        else -> 0
    }

    val colStep = when {
        end.col > start.col -> 1
        end.col < start.col -> -1
        else -> 0
    }

    var row = start.row
    var col = start.col

    while (true) {
        cells.add(CellSelection(row, col))
        if (row == end.row && col == end.col) break
        row += rowStep
        col += colStep
    }

    return cells
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}

private fun generateWordSearchGrid(words: List<String>, gridSize: Int): Pair<Array<CharArray>, List<PlacedWord>> {
    val directions = WordDirection.entries.toTypedArray()
    val maxGridAttempts = 50

    // Keep trying to generate a complete grid
    repeat(maxGridAttempts) {
        val grid = Array(gridSize) { CharArray(gridSize) { ' ' } }
        val placedWords = mutableListOf<PlacedWord>()

        // Sort words by length (longer first for better placement)
        val sortedWords = words.sortedByDescending { it.length }

        var allPlaced = true
        for (word in sortedWords) {
            val placed = tryPlaceWord(grid, word, directions, gridSize, placedWords)
            if (!placed) {
                allPlaced = false
                break
            }
        }

        // If all words were placed, fill remaining cells and return
        if (allPlaced && placedWords.size == words.size) {
            fillEmptyCells(grid, gridSize)
            return Pair(grid, placedWords)
        }
    }

    // Fallback: generate grid with as many words as possible
    val grid = Array(gridSize) { CharArray(gridSize) { ' ' } }
    val placedWords = mutableListOf<PlacedWord>()
    val sortedWords = words.sortedByDescending { it.length }

    for (word in sortedWords) {
        tryPlaceWord(grid, word, directions, gridSize, placedWords)
    }

    fillEmptyCells(grid, gridSize)
    return Pair(grid, placedWords)
}

private fun tryPlaceWord(
    grid: Array<CharArray>,
    word: String,
    directions: Array<WordDirection>,
    gridSize: Int,
    placedWords: MutableList<PlacedWord>
): Boolean {
    // First try random placement (faster for sparse grids)
    repeat(200) {
        val direction = directions[Random.nextInt(directions.size)]
        val startRow = Random.nextInt(gridSize)
        val startCol = Random.nextInt(gridSize)

        if (canPlaceWord(grid, word, startRow, startCol, direction, gridSize)) {
            placeWord(grid, word, startRow, startCol, direction)
            placedWords.add(PlacedWord(word, startRow, startCol, direction))
            return true
        }
    }

    // If random failed, try all possible positions systematically
    for (direction in directions) {
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                if (canPlaceWord(grid, word, row, col, direction, gridSize)) {
                    placeWord(grid, word, row, col, direction)
                    placedWords.add(PlacedWord(word, row, col, direction))
                    return true
                }
            }
        }
    }

    return false
}

private fun fillEmptyCells(grid: Array<CharArray>, gridSize: Int) {
    val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    for (row in 0 until gridSize) {
        for (col in 0 until gridSize) {
            if (grid[row][col] == ' ') {
                grid[row][col] = letters[Random.nextInt(letters.length)]
            }
        }
    }
}

private fun canPlaceWord(
    grid: Array<CharArray>,
    word: String,
    startRow: Int,
    startCol: Int,
    direction: WordDirection,
    gridSize: Int
): Boolean {
    val (rowDelta, colDelta) = getDirectionDeltas(direction)

    for (i in word.indices) {
        val row = startRow + i * rowDelta
        val col = startCol + i * colDelta

        // Check bounds
        if (row < 0 || row >= gridSize || col < 0 || col >= gridSize) {
            return false
        }

        // Check if cell is empty or has the same letter
        if (grid[row][col] != ' ' && grid[row][col] != word[i]) {
            return false
        }
    }

    return true
}

private fun placeWord(
    grid: Array<CharArray>,
    word: String,
    startRow: Int,
    startCol: Int,
    direction: WordDirection
) {
    val (rowDelta, colDelta) = getDirectionDeltas(direction)

    for (i in word.indices) {
        val row = startRow + i * rowDelta
        val col = startCol + i * colDelta
        grid[row][col] = word[i]
    }
}

private fun getDirectionDeltas(direction: WordDirection): Pair<Int, Int> {
    return when (direction) {
        WordDirection.HORIZONTAL -> Pair(0, 1)
        WordDirection.VERTICAL -> Pair(1, 0)
        WordDirection.DIAGONAL_DOWN -> Pair(1, 1)
        WordDirection.DIAGONAL_UP -> Pair(-1, 1)
    }
}
