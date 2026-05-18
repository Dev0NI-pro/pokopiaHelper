package com.dev0ni.pokopiahelper.data.model

data class BuildingTip(
    val id: Int,
    val category: TipCategory,
    val title: String,
    val body: String,
    val emoji: String = ""
)

enum class TipCategory(val displayName: String) {
    BASICS("Building Basics"),
    ROOMS("Room Rules"),
    HABITATS("Habitats"),
    TERRAIN("Terrain & Landscaping"),
    ADVANCED("Advanced Tips"),
    POKEMON("Pokémon Attraction")
}
