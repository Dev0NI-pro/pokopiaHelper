package com.dev0ni.pokopiahelper.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

private data class VideoTab(val label: String, val url: String)

private val VIDEO_TABS = listOf(
    VideoTab("House Builds",  "https://www.youtube.com/results?search_query=pokopia+house+build+tutorial"),
    VideoTab("Island Design", "https://www.youtube.com/results?search_query=pokopia+island+design+ideas"),
    VideoTab("All Pokémon",   "https://www.youtube.com/results?search_query=pokopia+all+pokemon+how+to+get"),
    VideoTab("Biome Tips",    "https://www.youtube.com/results?search_query=pokopia+biome+building+tips"),
    VideoTab("TikTok",        "https://www.tiktok.com/search?q=pokopia+build")
)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoHubScreen(navController: NavController) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val currentUrl = VIDEO_TABS[selectedTab].url

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Hub", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Tab row (scrollable)
            ScrollableTabRow(selectedTabIndex = selectedTab) {
                VIDEO_TABS.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab.label) }
                    )
                }
            }

            // WebView
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                        }
                        loadUrl(currentUrl)
                    }
                },
                update = { webView ->
                    if (webView.url != currentUrl) webView.loadUrl(currentUrl)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
