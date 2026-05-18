package com.dev0ni.pokopiahelper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dev0ni.pokopiahelper.data.model.BiomeType
import com.dev0ni.pokopiahelper.data.model.Pokemon
import com.dev0ni.pokopiahelper.viewmodel.CaughtFilter
import com.dev0ni.pokopiahelper.viewmodel.PokedexViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(navController: NavController) {
    val vm: PokedexViewModel = viewModel()
    val state by vm.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokédex", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Text(
                        "${state.caughtCount}/${state.totalCount}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = vm::onSearch,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Rechercher un Pokémon...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { vm.onSearch("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp)
            )

            LinearProgressIndicator(
                progress = { if (state.totalCount > 0) state.caughtCount.toFloat() / state.totalCount else 0f },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(CaughtFilter.entries) { filter ->
                    FilterChip(
                        selected = state.caughtFilter == filter,
                        onClick = { vm.onCaughtFilter(filter) },
                        label = {
                            Text(when (filter) {
                                CaughtFilter.ALL     -> "Tous"
                                CaughtFilter.CAUGHT  -> "Attirés"
                                CaughtFilter.MISSING -> "Manquants"
                            })
                        }
                    )
                }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.selectedBiome == null,
                        onClick = { vm.onBiomeFilter(null) },
                        label = { Text("Tous les Biomes") }
                    )
                }
                items(BiomeType.entries) { biome ->
                    FilterChip(
                        selected = state.selectedBiome == biome,
                        onClick = { vm.onBiomeFilter(biome) },
                        label = { Text(biome.displayName.split(" ").first()) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier.size(10.dp).clip(CircleShape)
                                    .background(biome.color)
                            )
                        }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.pokemon, key = { it.id }) { pokemon ->
                    PokemonRow(pokemon = pokemon, onToggle = { vm.toggle(pokemon) })
                }
            }
        }
    }
}

@Composable
private fun PokemonRow(pokemon: Pokemon, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (pokemon.isCaught)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "#%03d".format(pokemon.ndex),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.width(40.dp)
            )

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (pokemon.isCaught) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(pokemon.name, fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium)
                Text(pokemon.habitat, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                pokemon.types.forEach { type ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(type.color.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(type.displayName, color = Color.White,
                            fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(pokemon.biome.color)
            )

            Checkbox(
                checked = pokemon.isCaught,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
