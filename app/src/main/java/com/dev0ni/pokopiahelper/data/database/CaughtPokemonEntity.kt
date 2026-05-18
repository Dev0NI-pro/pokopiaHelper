package com.dev0ni.pokopiahelper.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caught_pokemon")
data class CaughtPokemonEntity(
    @PrimaryKey val pokemonId: Int,
    val caughtAt: Long = System.currentTimeMillis()
)
