package com.dev0ni.pokopiahelper.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev0ni.pokopiahelper.ui.screens.*

@Composable
fun PokopiaNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route)          { HomeScreen(navController) }
        composable(Screen.Pokedex.route)       { PokedexScreen(navController) }
        composable(Screen.BiomeGuide.route)    { BiomeGuideScreen(navController) }
        composable(Screen.BuildingGuide.route) { BuildingGuideScreen(navController) }
        composable(Screen.VideoHub.route)      { VideoHubScreen(navController) }
        composable(Screen.MapEditor.route)     { MapEditorScreen(navController) }
    }
}
