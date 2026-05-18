package com.dev0ni.pokopiahelper.data.repository

import com.dev0ni.pokopiahelper.data.database.AppDatabase
import com.dev0ni.pokopiahelper.data.database.MapPixelEntity
import kotlinx.coroutines.flow.Flow

class MapRepository(db: AppDatabase) {

    private val dao = db.mapPixelDao()

    val pixels: Flow<List<MapPixelEntity>> = dao.observeAllPixels()

    suspend fun paintPixel(cellIndex: Int, colorArgb: Int, biomeName: String) {
        dao.upsertPixel(MapPixelEntity(cellIndex, colorArgb, biomeName))
    }

    suspend fun saveAll(pixels: List<MapPixelEntity>) = dao.upsertAll(pixels)

    suspend fun clearMap() = dao.clearAll()
}
