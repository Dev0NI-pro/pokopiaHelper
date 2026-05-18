package com.dev0ni.pokopiahelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.dev0ni.pokopiahelper.ui.navigation.PokopiaNavGraph
import com.dev0ni.pokopiahelper.ui.theme.PokopiaHelperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokopiaHelperTheme {
                PokopiaNavGraph()
            }
        }
    }
}
