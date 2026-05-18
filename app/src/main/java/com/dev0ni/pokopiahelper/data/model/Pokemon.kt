package com.dev0ni.pokopiahelper.data.model

data class Pokemon(
    val id: Int,
    val ndex: Int,
    val name: String,
    val types: List<PokemonType>,
    val biome: BiomeType,
    val habitat: String,
    val attractedBy: String,
    val isCaught: Boolean = false
)
