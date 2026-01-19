package com.example.alphabettracer.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabettracer.content.ColoringContent
import com.example.alphabettracer.content.ColoringPage
import com.example.alphabettracer.data.ColoringStorage
import com.example.alphabettracer.ui.components.ColoringCanvas
import com.example.alphabettracer.ui.components.ColoringTutorial
import com.example.alphabettracer.ui.components.DrawingPath

/**
 * Coloring Book Screen - Kids can select shapes and color them with free-form drawing
 */
@Composable
fun ColoringScreen(
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var selectedPage by remember { mutableStateOf<ColoringPage?>(null) }
    var selectedColor by remember { mutableStateOf(ColoringContent.colorPalette.first().color) }
    var brushSize by remember { mutableFloatStateOf(20f) }
    var isEraser by remember { mutableStateOf(false) }
    val drawingPaths = remember { mutableStateListOf<DrawingPath>() }
    var showTutorial by remember { mutableStateOf(false) }

    // Animation states
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
        // Check if tutorial should be shown on first launch
        if (!ColoringStorage.hasShownTutorial(context)) {
            showTutorial = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedPage == null) {
            // Shape selection screen
            ShapeSelectionScreen(
                showContent = showContent,
                onShapeSelected = { page ->
                    selectedPage = page
                    drawingPaths.clear()
                }
            )
        } else {
            // Coloring screen
            ColoringWorkspace(
                page = selectedPage!!,
                selectedColor = selectedColor,
                brushSize = brushSize,
                isEraser = isEraser,
                drawingPaths = drawingPaths,
                onColorSelected = {
                    selectedColor = it
                    isEraser = false
                },
                onBrushSizeChanged = { brushSize = it },
                onEraserToggle = { isEraser = !isEraser },
                onClear = {
                    drawingPaths.clear()
                },
                onUndo = {
                    if (drawingPaths.isNotEmpty()) {
                        drawingPaths.removeAt(drawingPaths.lastIndex)
                    }
                },
                onBack = {
                    selectedPage = null
                    drawingPaths.clear()
                }
            )
        }

        // Tutorial overlay for first-time users
        ColoringTutorial(
            isVisible = showTutorial,
            onDismiss = {
                showTutorial = false
                ColoringStorage.markTutorialShown(context)
            },
            onSkip = {
                showTutorial = false
                ColoringStorage.markTutorialShown(context)
            }
        )
    }
}

@Composable
private fun ShapeSelectionScreen(
    showContent: Boolean,
    onShapeSelected: (ColoringPage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(400)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🎨", fontSize = 32.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Coloring Book",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6200EE)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("🖌️", fontSize = 32.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Choose a picture to color!",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }

        // Shape grid
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(tween(400))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(ColoringContent.coloringPages) { page ->
                    ShapeCard(
                        page = page,
                        onClick = { onShapeSelected(page) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ShapeCard(
    page: ColoringPage,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "card_scale"
    )

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                page.emoji,
                fontSize = 48.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                page.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )
            // Difficulty indicator
            Row {
                repeat(page.difficulty) {
                    Text("⭐", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun ColoringWorkspace(
    page: ColoringPage,
    selectedColor: Color,
    brushSize: Float,
    isEraser: Boolean,
    drawingPaths: MutableList<DrawingPath>,
    onColorSelected: (Color) -> Unit,
    onBrushSizeChanged: (Float) -> Unit,
    onEraserToggle: () -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Header with shape name and controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(page.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    page.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            Row {
                // Undo button
                IconButton(
                    onClick = onUndo,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFF3E0), CircleShape)
                ) {
                    Text("↩️", fontSize = 20.sp)
                }

                Spacer(Modifier.width(4.dp))

                // Clear button
                IconButton(
                    onClick = onClear,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFFFEBEE), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Clear",
                        tint = Color(0xFFF44336)
                    )
                }

                Spacer(Modifier.width(4.dp))

                // Back button
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6200EE)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Change", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Canvas area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            ColoringCanvas(
                shapeId = page.id,
                selectedColor = selectedColor,
                brushSize = brushSize,
                isEraser = isEraser,
                drawingPaths = drawingPaths,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Tools section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Brush size slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🖌️", fontSize = 16.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Brush Size",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.width(8.dp))
                    Slider(
                        value = brushSize,
                        onValueChange = onBrushSizeChanged,
                        valueRange = 5f..50f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF6200EE),
                            activeTrackColor = Color(0xFF6200EE)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    // Brush preview
                    Box(
                        modifier = Modifier
                            .size(brushSize.dp.coerceIn(10.dp, 40.dp))
                            .background(
                                if (isEraser) Color.Gray else selectedColor,
                                CircleShape
                            )
                            .border(1.dp, Color.Gray, CircleShape)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Eraser and Color palette row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Eraser button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .shadow(
                                elevation = if (isEraser) 8.dp else 2.dp,
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(if (isEraser) Color(0xFFE0E0E0) else Color.White)
                            .border(
                                width = if (isEraser) 3.dp else 1.dp,
                                color = if (isEraser) Color(0xFF333333) else Color.Gray.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .clickable { onEraserToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧹", fontSize = 24.sp)
                    }

                    Spacer(Modifier.width(12.dp))

                    // Color palette
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(ColoringContent.colorPalette) { colorOption ->
                            ColorButton(
                                color = colorOption.color,
                                isSelected = selectedColor == colorOption.color && !isEraser,
                                onClick = { onColorSelected(colorOption.color) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "color_scale"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .shadow(
                elevation = if (isSelected) 8.dp else 2.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) Color(0xFF333333) else Color.Gray.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color == Color.White || color == Color(0xFFFFEB3B)) Color.Black else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
