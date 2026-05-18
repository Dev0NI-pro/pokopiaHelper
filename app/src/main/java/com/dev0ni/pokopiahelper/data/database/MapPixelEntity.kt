package com.dev0ni.pokopiahelper.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map_pixels")
data class MapPixelEntity(
    @PrimaryKey(autoGenerate = false)
    val cellIndex: Int,   // row * GRID_SIZE + col
    val colorArgb: Int,   // ARGB packed int
    val biomeName: String
)
