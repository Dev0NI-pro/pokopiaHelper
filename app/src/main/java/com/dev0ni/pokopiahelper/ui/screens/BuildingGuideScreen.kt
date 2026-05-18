package com.dev0ni.pokopiahelper.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dev0ni.pokopiahelper.data.PokopiaData
import com.dev0ni.pokopiahelper.data.model.TipCategory

private data class WebArticle(
    val title: String,
    val description: String,
    val url: String,
    val lang: String
)

private val WEB_ARTICLES = listOf(
    WebArticle("Guide complet Pokopia", "Tutoriels et astuces en vidéo", "https://www.youtube.com/results?search_query=pokopia+guide+complet+francais", "FR"),
    WebArticle("Attirer tous les Pokémon", "Où trouver chaque Pokémon", "https://www.youtube.com/results?search_query=pokopia+tous+les+pokemon+comment+attirer", "FR"),
    WebArticle("Idées de constructions", "Inspiration pour vos bâtiments", "https://www.youtube.com/results?search_query=pokopia+idees+constructions+maison", "FR"),
    WebArticle("Pokopia Full Pokédex Guide", "All 311 Pokémon locations", "https://www.youtube.com/results?search_query=pokopia+full+pokedex+guide+all+pokemon", "EN"),
    WebArticle("Pokopia Building Tips", "Advanced habitat construction", "https://www.youtube.com/results?search_query=pokopia+building+tips+habitat+tutorial", "EN"),
    WebArticle("Pokopia Wiki - Habitats", "Base de données des habitats", "https://www.google.com/search?q=pokopia+habitat+wiki+guide", "FR/EN"),
    WebArticle("Pokopia sur Gamekult", "Actualités et tests fréquents", "https://www.gamekult.com/recherche?q=pokopia", "FR"),
    WebArticle("Legendaries Guide", "How to unlock all legendaries", "https://www.youtube.com/results?search_query=pokopia+legendary+pokemon+how+to+unlock", "EN")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildingGuideScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf<TipCategory?>(null) }
    var showArticles by remember { mutableStateOf(false) }

    val tips = PokopiaData.buildingTips
    val filtered = if (selectedCategory == null) tips
    else tips.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guide de Construction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Tab: Conseils / Articles
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !showArticles,
                    onClick = { showArticles = false },
                    label = { Text("📋 Conseils (${tips.size})") }
                )
                FilterChip(
                    selected = showArticles,
                    onClick = { showArticles = true },
                    label = { Text("🌐 Articles Web") }
                )
            }

            if (showArticles) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(WEB_ARTICLES) { article ->
                        ArticleCard(article)
                    }
                }
            } else {
                // Category filter
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("Tout (${tips.size})") }
                        )
                    }
                    items(TipCategory.entries) { cat ->
                        val count = tips.count { it.category == cat }
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = if (selectedCategory == cat) null else cat },
                            label = { Text("${cat.displayName} ($count)") }
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { tip ->
                        TipCard(tip.emoji, tip.title, tip.body, tip.category)
                    }
                }
            }
        }
    }
}

@Composable
private fun ArticleCard(article: WebArticle) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.OpenInBrowser, null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(article.title, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall)
                    SuggestionChip(
                        onClick = {},
                        label = { Text(article.lang, fontSize = 9.sp) }
                    )
                }
                Text(article.description, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
private fun TipCard(emoji: String, title: String, body: String, category: TipCategory) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.weight(1f))
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                SuggestionChip(
                    onClick = {},
                    label = { Text(category.displayName, fontSize = 10.sp) },
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (expanded) {
                    Spacer(Modifier.height(8.dp))
                    Text(body, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                }
            }
        }
    }
}
