package com.dev0ni.pokopiahelper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev0ni.pokopiahelper.ui.navigation.Screen
import com.dev0ni.pokopiahelper.viewmodel.PokedexViewModel

data class FeatureCard(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val vm: PokedexViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    val cards = listOf(
        FeatureCard("Pokédex", "311 Pokémon tracker", Icons.Default.Pets,
            listOf(Color(0xFF11998E), Color(0xFF38EF7D)), Screen.Pokedex.route),
        FeatureCard("Biome Guide", "5 areas & habitats", Icons.Default.Landscape,
            listOf(Color(0xFF134E5E), Color(0xFF71B280)), Screen.BiomeGuide.route),
        FeatureCard("Building Guide", "25 tips & tricks", Icons.Default.Construction,
            listOf(Color(0xFFB79891), Color(0xFF94716B)), Screen.BuildingGuide.route),
        FeatureCard("Video Hub", "YouTube & TikTok", Icons.Default.PlayCircle,
            listOf(Color(0xFFFF512F), Color(0xFFDD2476)), Screen.VideoHub.route),
        FeatureCard("Island Map", "S Pen pixel editor", Icons.Default.Draw,
            listOf(Color(0xFF4776E6), Color(0xFF8E54E9)), Screen.MapEditor.route),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Pokopia Helper", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Progress card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Your Progress", style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${state.caughtCount} / ${state.totalCount}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("Pokémon attracted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (state.totalCount > 0) state.caughtCount.toFloat() / state.totalCount else 0f },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(8.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Feature grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cards) { card ->
                    FeatureCardItem(card) { navController.navigate(card.route) }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FeatureCardItem(card: FeatureCard, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(card.gradient))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(card.icon, contentDescription = null, tint = Color.White,
                modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(10.dp))
            Text(card.title, color = Color.White, fontWeight = FontWeight.Bold,
                fontSize = 15.sp, textAlign = TextAlign.Center)
            Text(card.subtitle, color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp, textAlign = TextAlign.Center)
        }
    }
}
