package com.dev0ni.pokopiahelper.data.model

import androidx.compose.ui.graphics.Color

enum class BiomeType(
    val displayName: String,
    val color: Color,
    val description: String,
    val unlockCondition: String
) {
    WITHERED_WASTELAND(
        "Withered Wasteland",
        Color(0xFF8B6914),
        "The first biome you restore. Dry, ruined terrain with Ghost and Ground-type Pokémon waiting for habitats.",
        "Available from the start"
    ),
    BLEAK_BEACH(
        "Bleak Beach",
        Color(0xFFE8D44D),
        "A dark beach where the electricity went out. Restore power to attract Water and Electric-type Pokémon.",
        "Complete Withered Wasteland main quest"
    ),
    PALETTE_TOWN(
        "Palette Town",
        Color(0xFF7DCEA0),
        "The sandbox biome with no main quest. Build freely and attract a wide variety of common Pokémon.",
        "Unlocked automatically after tutorial"
    ),
    ROCKY_RIDGES(
        "Rocky Ridges",
        Color(0xFF7F8C8D),
        "Mountainous terrain rich in Rock, Fighting and Steel-type Pokémon. Build structured habitats to attract them.",
        "Complete Bleak Beach main quest"
    ),
    SPARKLING_SKYLANDS(
        "Sparkling Skylands",
        Color(0xFF85C1E9),
        "The final area themed after Celadon City in the sky. Home to Flying, Dragon and Psychic-type Pokémon.",
        "Complete Rocky Ridges main quest"
    )
}
