package com.dev0ni.pokopiahelper.viewmodel

import android.app.Application
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dev0ni.pokopiahelper.data.database.AppDatabase
import com.dev0ni.pokopiahelper.data.database.MapPixelEntity
import com.dev0ni.pokopiahelper.data.model.BiomeType
import com.dev0ni.pokopiahelper.data.repository.MapRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

const val GRID_SIZE = 64

data class MapEditorUiState(
    val pixels: IntArray = IntArray(GRID_SIZE * GRID_SIZE) { Color.White.toArgb() },
    val selectedBiome: BiomeType = BiomeType.PALETTE_TOWN,
    val brushSize: Int = 1,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

class MapEditorViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = MapRepository(AppDatabase.getInstance(app))

    // Undo/redo stacks — each entry is a full pixel snapshot
    private val undoStack = ArrayDeque<IntArray>()
    private val redoStack = ArrayDeque<IntArray>()
    private val MAX_HISTORY = 30

    private val _state = MutableStateFlow(MapEditorUiState())
    val uiState: StateFlow<MapEditorUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repo.pixels.collect { entities ->
                if (entities.isNotEmpty()) {
                    val pixels = IntArray(GRID_SIZE * GRID_SIZE) { Color.White.toArgb() }
                    entities.forEach { e -> if (e.cellIndex < pixels.size) pixels[e.cellIndex] = e.colorArgb }
                    _state.update { it.copy(pixels = pixels) }
                }
            }
        }
    }

    fun selectBiome(biome: BiomeType) = _state.update { it.copy(selectedBiome = biome) }
    fun setBrushSize(size: Int) = _state.update { it.copy(brushSize = size.coerceIn(1, 5)) }

    fun paint(cellIndex: Int) {
        if (cellIndex < 0 || cellIndex >= GRID_SIZE * GRID_SIZE) return
        val current = _state.value
        pushUndo(current.pixels)

        val color = current.selectedBiome.color.toArgb()
        val brushSize = current.brushSize
        val newPixels = current.pixels.copyOf()

        val col = cellIndex % GRID_SIZE
        val row = cellIndex / GRID_SIZE
        for (dr in -brushSize + 1 until brushSize) {
            for (dc in -brushSize + 1 until brushSize) {
                val r = row + dr; val c = col + dc
                if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                    newPixels[r * GRID_SIZE + c] = color
                }
            }
        }
        _state.update { it.copy(pixels = newPixels, canUndo = undoStack.isNotEmpty(), canRedo = false) }

        viewModelScope.launch {
            repo.paintPixel(cellIndex, color, current.selectedBiome.name)
        }
    }

    fun undo() {
        val prev = undoStack.removeLastOrNull() ?: return
        redoStack.addLast(_state.value.pixels.copyOf())
        _state.update { it.copy(pixels = prev, canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
        persistAll(_state.value.pixels)
    }

    fun redo() {
        val next = redoStack.removeLastOrNull() ?: return
        undoStack.addLast(_state.value.pixels.copyOf())
        _state.update { it.copy(pixels = next, canUndo = undoStack.isNotEmpty(), canRedo = redoStack.isNotEmpty()) }
        persistAll(_state.value.pixels)
    }

    fun clearMap() {
        pushUndo(_state.value.pixels)
        val empty = IntArray(GRID_SIZE * GRID_SIZE) { Color.White.toArgb() }
        _state.update { it.copy(pixels = empty, canUndo = undoStack.isNotEmpty(), canRedo = false) }
        viewModelScope.launch { repo.clearMap() }
    }

    private fun pushUndo(snapshot: IntArray) {
        if (undoStack.size >= MAX_HISTORY) undoStack.removeFirst()
        undoStack.addLast(snapshot.copyOf())
        redoStack.clear()
    }

    private fun persistAll(pixels: IntArray) = viewModelScope.launch {
        val white = Color.White.toArgb()
        val entities = pixels.indices.mapNotNull { index ->
            val argb = pixels[index]
            if (argb != white) MapPixelEntity(index, argb, "") else null
        }
        repo.clearMap()
        repo.saveAll(entities)
    }
}
