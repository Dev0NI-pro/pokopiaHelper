package com.dev0ni.pokopiahelper.data.model

import androidx.compose.ui.graphics.Color

enum class BiomeType(
    val displayName: String,
    val color: Color,
    val description: String,
    val unlockCondition: String
) {
    WITHERED_WASTELAND(
        "Terres Dévastées",
        Color(0xFF8B6914),
        "Le premier biome à restaurer. Terrain sec et en ruines où des Pokémon de type Spectre et Sol attendent des habitats.",
        "Disponible dès le début"
    ),
    BLEAK_BEACH(
        "Plage Morne",
        Color(0xFFE8D44D),
        "Une plage sombre où l'électricité a disparu. Rétablissez le courant pour attirer les Pokémon de type Eau et Électrik.",
        "Terminer la quête principale des Terres Dévastées"
    ),
    PALETTE_TOWN(
        "Bourg Palette",
        Color(0xFF7DCEA0),
        "Le biome bac à sable sans quête principale. Construisez librement et attirez une grande variété de Pokémon.",
        "Débloqué automatiquement après le tutoriel"
    ),
    ROCKY_RIDGES(
        "Crêtes Rocheuses",
        Color(0xFF7F8C8D),
        "Terrain montagneux riche en Pokémon de type Roche, Combat et Acier. Construisez des habitats structurés pour les attirer.",
        "Terminer la quête principale de la Plage Morne"
    ),
    SPARKLING_SKYLANDS(
        "Archipel Céleste",
        Color(0xFF85C1E9),
        "La zone finale inspirée de Céladopole dans les cieux. Abrite des Pokémon de type Vol, Dragon et Psy.",
        "Terminer la quête principale des Crêtes Rocheuses"
    )
}
