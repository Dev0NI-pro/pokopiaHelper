package com.dev0ni.pokopiahelper.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dev0ni.pokopiahelper.data.database.AppDatabase
import com.dev0ni.pokopiahelper.data.model.BiomeType
import com.dev0ni.pokopiahelper.data.model.Pokemon
import com.dev0ni.pokopiahelper.data.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class CaughtFilter { ALL, CAUGHT, MISSING }

data class PokedexUiState(
    val pokemon: List<Pokemon> = emptyList(),
    val searchQuery: String = "",
    val selectedBiome: BiomeType? = null,
    val caughtFilter: CaughtFilter = CaughtFilter.ALL,
    val caughtCount: Int = 0,
    val totalCount: Int = 311
)

class PokedexViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PokemonRepository(AppDatabase.getInstance(app))

    private val _search = MutableStateFlow("")
    private val _biome = MutableStateFlow<BiomeType?>(null)
    private val _caughtFilter = MutableStateFlow(CaughtFilter.ALL)

    val uiState: StateFlow<PokedexUiState> = combine(
        repo.allPokemonWithStatus,
        _search,
        _biome,
        _caughtFilter
    ) { allPokemon, query, biome, filter ->
        val filtered = allPokemon
            .filter { p -> query.isBlank() || p.name.contains(query, ignoreCase = true) }
            .filter { p -> biome == null || p.biome == biome }
            .filter { p ->
                when (filter) {
                    CaughtFilter.ALL     -> true
                    CaughtFilter.CAUGHT  -> p.isCaught
                    CaughtFilter.MISSING -> !p.isCaught
                }
            }
            .sortedBy { it.ndex }
        PokedexUiState(
            pokemon = filtered,
            searchQuery = query,
            selectedBiome = biome,
            caughtFilter = filter,
            caughtCount = allPokemon.count { it.isCaught },
            totalCount = allPokemon.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PokedexUiState())

    fun onSearch(q: String) { _search.value = q }
    fun onBiomeFilter(b: BiomeType?) { _biome.value = b }
    fun onCaughtFilter(f: CaughtFilter) { _caughtFilter.value = f }

    fun toggle(pokemon: Pokemon) = viewModelScope.launch {
        repo.toggle(pokemon)
    }
}
