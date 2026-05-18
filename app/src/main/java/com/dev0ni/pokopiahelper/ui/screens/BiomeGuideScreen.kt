package com.dev0ni.pokopiahelper.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev0ni.pokopiahelper.data.PokopiaData
import com.dev0ni.pokopiahelper.data.model.BiomeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomeGuideScreen(navController: NavController) {
    var expandedBiomes by remember { mutableStateOf(setOf<BiomeType>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biome Guide", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(BiomeType.entries) { biome ->
                val isExpanded = biome in expandedBiomes
                val biomePokemon = PokopiaData.allPokemon.filter { it.biome == biome }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedBiomes = if (isExpanded) expandedBiomes - biome
                                    else expandedBiomes + biome
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(biome.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Landscape, null,
                                    tint = Color.White, modifier = Modifier.size(24.dp))
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(biome.displayName, fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium)
                                Text("${biomePokemon.size} Pokémon",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            }

                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider()
                                Spacer(Modifier.height(4.dp))

                                // Description
                                Text(biome.description, style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                                // Unlock condition
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Lock, null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.secondary)
                                    Text(biome.unlockCondition,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.secondary)
                                }

                                Spacer(Modifier.height(4.dp))
                                Text("Pokémon in this biome:",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall)

                                // Pokemon chips in wrapping rows (simulated with LazyRow batches)
                                val chunks = biomePokemon.chunked(4)
                                chunks.forEach { chunk ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        chunk.forEach { pokemon ->
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(pokemon.name, fontSize = 11.sp) },
                                                leadingIcon = {
                                                    Box(
                                                        modifier = Modifier.size(8.dp).clip(CircleShape)
                                                            .background(pokemon.types.first().color)
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(4.dp))
                                Text("Key habitats to build:",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall)

                                biomePokemon.map { it.attractedBy }.distinct().take(6).forEach { habitat ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Build, null,
                                            modifier = Modifier.size(14.dp),
                                            tint = biome.color)
                                        Text(habitat, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
