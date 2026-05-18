package com.dev0ni.pokopiahelper.ui.screens

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev0ni.pokopiahelper.data.model.BiomeType
import com.dev0ni.pokopiahelper.viewmodel.GRID_SIZE
import com.dev0ni.pokopiahelper.viewmodel.MapEditorViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun MapEditorScreen(navController: NavController) {
    val vm: MapEditorViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showBrushSlider by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Map?") },
            text = { Text("This will erase your entire island design.") },
            confirmButton = {
                TextButton(onClick = { vm.clearMap(); showClearDialog = false }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Island Map Editor", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { vm.undo() }, enabled = state.canUndo) {
                        Icon(Icons.Default.Undo, "Undo")
                    }
                    IconButton(onClick = { vm.redo() }, enabled = state.canRedo) {
                        Icon(Icons.Default.Redo, "Redo")
                    }
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(Icons.Default.DeleteForever, "Clear", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            // Biome color palette
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(BiomeType.entries) { biome ->
                    BiomePaletteItem(
                        biome = biome,
                        isSelected = state.selectedBiome == biome,
                        onClick = { vm.selectBiome(biome) }
                    )
                }
            }

            // Brush size + selected biome indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(state.selectedBiome.color)
                )
                Text(
                    state.selectedBiome.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                Text("Brush: ${state.brushSize}", style = MaterialTheme.typography.labelMedium)
                IconButton(onClick = { vm.setBrushSize(state.brushSize - 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Remove, null, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = { vm.setBrushSize(state.brushSize + 1) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                }
            }

            // S Pen tip
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer)
                    Text(
                        "S Pen tip: draw with stylus for pixel-perfect precision. Brush size 1 = single cell.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            // Main pixel canvas — fills remaining space
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8E8E8))
            ) {
                val pixelSnapshot = state.pixels.copyOf()
                val selectedBiome = state.selectedBiome

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { canvasSize = it }
                        .pointerInteropFilter { event ->
                            if (canvasSize == IntSize.Zero) return@pointerInteropFilter false
                            val cellSize = canvasSize.width.toFloat() / GRID_SIZE
                            fun handleTouch(x: Float, y: Float): Boolean {
                                val col = (x / cellSize).toInt().coerceIn(0, GRID_SIZE - 1)
                                val row = (y / cellSize).toInt().coerceIn(0, GRID_SIZE - 1)
                                vm.paint(row * GRID_SIZE + col)
                                return true
                            }
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                    // Support S Pen (TOOL_TYPE_STYLUS) and finger (TOOL_TYPE_FINGER)
                                    for (i in 0 until event.pointerCount) {
                                        val toolType = event.getToolType(i)
                                        if (toolType == MotionEvent.TOOL_TYPE_STYLUS ||
                                            toolType == MotionEvent.TOOL_TYPE_FINGER) {
                                            handleTouch(event.getX(i), event.getY(i))
                                        }
                                    }
                                    true
                                }
                                else -> false
                            }
                        }
                ) {
                    drawPixelGrid(pixelSnapshot, canvasSize)
                }
            }

            // Legend row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Legend:", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                BiomeType.entries.forEach { biome ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(10.dp).clip(CircleShape)
                                .background(biome.color)
                        )
                        Text(
                            biome.displayName.split(" ").first(),
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPixelGrid(pixels: IntArray, canvasSize: IntSize) {
    if (canvasSize == IntSize.Zero) return
    val cellW = canvasSize.width.toFloat() / GRID_SIZE
    val cellH = canvasSize.height.toFloat() / GRID_SIZE

    for (row in 0 until GRID_SIZE) {
        for (col in 0 until GRID_SIZE) {
            val idx = row * GRID_SIZE + col
            val color = Color(pixels[idx])
            drawRect(
                color = color,
                topLeft = Offset(col * cellW, row * cellH),
                size = Size(cellW, cellH)
            )
        }
    }

    // Subtle grid lines every 8 cells
    val gridLineColor = Color.Black.copy(alpha = 0.06f)
    for (i in 0..GRID_SIZE step 8) {
        val x = i * cellW
        val y = i * cellH
        drawLine(gridLineColor, Offset(x, 0f), Offset(x, canvasSize.height.toFloat()), strokeWidth = 0.5f)
        drawLine(gridLineColor, Offset(0f, y), Offset(canvasSize.width.toFloat(), y), strokeWidth = 0.5f)
    }
}

@Composable
private fun BiomePaletteItem(
    biome: BiomeType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(if (isSelected) 42.dp else 36.dp)
                .clip(CircleShape)
                .background(biome.color)
                .then(
                    if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    else Modifier
                )
        )
        Text(
            biome.displayName.split(" ").first(),
            fontSize = 9.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
