package com.dev0ni.pokopiahelper.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CaughtPokemonDao {
    @Query("SELECT pokemonId FROM caught_pokemon")
    fun observeCaughtIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markCaught(entity: CaughtPokemonEntity)

    @Query("DELETE FROM caught_pokemon WHERE pokemonId = :id")
    suspend fun markMissing(id: Int)

    @Query("SELECT COUNT(*) FROM caught_pokemon")
    suspend fun countCaught(): Int
}
