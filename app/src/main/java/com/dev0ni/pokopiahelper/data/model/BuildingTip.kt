package com.dev0ni.pokopiahelper.data.model

data class BuildingTip(
    val id: Int,
    val category: TipCategory,
    val title: String,
    val body: String,
    val emoji: String = ""
)

enum class TipCategory(val displayName: String) {
    BASICS("Bases de Construction"),
    ROOMS("Règles des Pièces"),
    HABITATS("Habitats"),
    TERRAIN("Terrain & Paysage"),
    ADVANCED("Conseils Avancés"),
    POKEMON("Attraction Pokémon")
}
