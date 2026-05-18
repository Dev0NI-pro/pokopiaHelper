# Pokopia Helper

An Android companion app for the Pokopia game, built with Kotlin and Jetpack Compose.

## Features

- **Pokedex** — Browse all Pokémon with their types, biomes, and habitats. Track which ones you've caught.
- **Biome Guide** — Find Pokémon by biome type to optimize your exploration.
- **Building Guide** — Tips and advice for building in Pokopia.
- **Video Hub** — Curated video resources for the game.
- **Map Editor** — Draw and annotate your own Pokopia maps.

## Tech Stack

- Kotlin
- Jetpack Compose
- Room (local database for caught Pokémon and map data)
- MVVM architecture (ViewModel + Repository pattern)
- Navigation Compose

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Build and run on an emulator or physical device (API 24+)

## Project Structure

```
app/src/main/java/com/dev0ni/pokopiahelper/
├── data/
│   ├── model/          # Data classes (Pokemon, PokemonType, BiomeType...)
│   ├── database/       # Room entities and DAOs
│   ├── repository/     # Repositories (Pokemon, Map)
│   └── PokopiaData.kt  # Static game data
├── viewmodel/          # ViewModels (Pokedex, MapEditor)
└── ui/
    ├── screens/        # Compose screens
    ├── navigation/     # NavGraph and Screen routes
    └── theme/          # Colors, typography, theme
```
