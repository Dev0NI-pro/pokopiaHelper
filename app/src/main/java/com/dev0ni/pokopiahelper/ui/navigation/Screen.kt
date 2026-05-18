package com.dev0ni.pokopiahelper.ui.navigation

sealed class Screen(val route: String) {
    object Home          : Screen("home")
    object Pokedex       : Screen("pokedex")
    object BiomeGuide    : Screen("biome_guide")
    object BuildingGuide : Screen("building_guide")
    object VideoHub      : Screen("video_hub")
    object MapEditor     : Screen("map_editor")
}
