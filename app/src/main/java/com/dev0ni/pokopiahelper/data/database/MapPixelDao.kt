package com.dev0ni.pokopiahelper.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MapPixelDao {
    @Query("SELECT * FROM map_pixels")
    fun observeAllPixels(): Flow<List<MapPixelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPixel(pixel: MapPixelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pixels: List<MapPixelEntity>)

    @Query("DELETE FROM map_pixels")
    suspend fun clearAll()
}
