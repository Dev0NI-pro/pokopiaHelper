package com.dev0ni.pokopiahelper.data.repository

import com.dev0ni.pokopiahelper.data.PokopiaData
import com.dev0ni.pokopiahelper.data.database.AppDatabase
import com.dev0ni.pokopiahelper.data.database.CaughtPokemonEntity
import com.dev0ni.pokopiahelper.data.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepository(db: AppDatabase) {

    private val dao = db.caughtPokemonDao()

    val allPokemonWithStatus: Flow<List<Pokemon>> =
        dao.observeCaughtIds().map { caughtIds ->
            val caughtSet = caughtIds.toSet()
            PokopiaData.allPokemon.map { p -> p.copy(isCaught = p.id in caughtSet) }
        }

    suspend fun toggle(pokemon: Pokemon) {
        if (pokemon.isCaught) dao.markMissing(pokemon.id)
        else dao.markCaught(CaughtPokemonEntity(pokemon.id))
    }

    suspend fun countCaught(): Int = dao.countCaught()
}
