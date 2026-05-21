package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.toArgb
import com.example.presentation.ViewModelFactory
import com.example.presentation.gallery.GalleryScreen
import com.example.presentation.gallery.GalleryViewModel
import com.example.presentation.generate.GenerateScreen
import com.example.presentation.generate.GenerateViewModel
import com.example.presentation.home.HomeScreen
import com.example.presentation.home.SplashScreen
import com.example.presentation.history.HistoryScreen
import com.example.presentation.history.HistoryViewModel
import com.example.presentation.navigation.Screen
import com.example.presentation.preview.PreviewScreen
import com.example.presentation.preview.PreviewViewModel
import com.example.presentation.settings.SettingsScreen
import com.example.presentation.settings.SettingsViewModel
import com.example.ui.theme.CustomGlassSurface
import com.example.ui.theme.DeepSpaceBlack
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PolarWhite
import com.example.ui.theme.PrimaryElectricViolet
import com.example.ui.theme.SecondaryCyberCyan
import com.example.ui.theme.SpaceSlateDark

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. EnableEdgeToEdge handles bezel and camera-notch transparent safe areas natively!
        enableEdgeToEdge()
        
        setContent {
            val factory = ViewModelFactory(applicationContext)
            
            // 2. Resolve ViewModels cleanly via the custom Factory
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val generateViewModel: GenerateViewModel = viewModel(factory = factory)
            val previewViewModel: PreviewViewModel = viewModel(factory = factory)
            val galleryViewModel: GalleryViewModel = viewModel(factory = factory)
            val historyViewModel: HistoryViewModel = viewModel(factory = factory)
            
            val isDarkTheme by settingsViewModel.isDarkMode.collectAsState()
            val customApiKey by settingsViewModel.customApiKey.collectAsState()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // 3. Conditionally display the Bottom Navigation Bar (Hide on splash and preview screens)
                val isBottomBarVisible = currentRoute != Screen.SPLASH && currentRoute?.startsWith("preview/") == false

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepSpaceBlack,
                    bottomBar = {
                        if (isBottomBarVisible) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(Color.Transparent, DeepSpaceBlack.copy(alpha = 0.95f))
                                        )
                                    )
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                NavigationBar(
                                    containerColor = CustomGlassSurface,
                                    tonalElevation = 12.dp,
                                    modifier = Modifier.testTag("bottom_nav_bar")
                                ) {
                                    val items = listOf(
                                        BottomNavItem("Home", "🏠", Screen.HOME, "nav_home"),
                                        BottomNavItem("Create", "⚡", Screen.GENERATE, "nav_create"),
                                        BottomNavItem("Gallery", "🖼️", Screen.GALLERY, "nav_gallery"),
                                        BottomNavItem("History", "💾", Screen.HISTORY, "nav_history"),
                                        BottomNavItem("Settings", "⚙️", Screen.SETTINGS, "nav_settings")
                                    )

                                    items.forEach { item ->
                                        val isSelected = currentRoute == item.route
                                        NavigationBarItem(
                                            selected = isSelected,
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                Text(text = item.emoji, fontSize = 20.sp)
                                            },
                                            label = {
                                                Text(
                                                    text = item.title,
                                                    style = MaterialTheme.typography.labelMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 10.sp,
                                                        color = if (isSelected) SecondaryCyberCyan else PolarWhite.copy(alpha = 0.5f)
                                                    )
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = SecondaryCyberCyan,
                                                unselectedIconColor = PolarWhite.copy(alpha = 0.4f),
                                                indicatorColor = PrimaryElectricViolet.copy(alpha = 0.15f)
                                            ),
                                            modifier = Modifier.testTag(item.testTag)
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SPLASH,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = if (isBottomBarVisible) innerPadding.calculateBottomPadding() else 0.dp)
                    ) {
                        // A: Splash Screen entrance
                        composable(Screen.SPLASH) {
                            SplashScreen(
                                onNavigateToHome = {
                                    navController.navigate(Screen.HOME) {
                                        popUpTo(Screen.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // B: Home Dashboard Screen
                        composable(Screen.HOME) {
                            HomeScreen(
                                onNavigateToCreate = { navController.navigate(Screen.GENERATE) },
                                onNavigateToSettings = { navController.navigate(Screen.SETTINGS) }
                            )
                        }

                        // C: Live Prompt Canvas Builder Generator
                        composable(Screen.GENERATE) {
                            GenerateScreen(
                                viewModel = generateViewModel,
                                customApiKey = customApiKey,
                                onNavigateToResult = { id ->
                                    navController.navigate(Screen.createPreviewRoute(id))
                                }
                            )
                        }

                        // D: Shared Grid Showcase Gallery
                        composable(Screen.GALLERY) {
                            GalleryScreen(
                                viewModel = galleryViewModel,
                                onNavigateToPreview = { id ->
                                    navController.navigate(Screen.createPreviewRoute(id))
                                },
                                onNavigateToCreate = { navController.navigate(Screen.GENERATE) }
                            )
                        }

                        // E: Prompt Histories Searches & sorting
                        composable(Screen.HISTORY) {
                            HistoryScreen(
                                viewModel = historyViewModel,
                                onNavigateToPreview = { id ->
                                    navController.navigate(Screen.createPreviewRoute(id))
                                },
                                onReusePrompt = { artwork ->
                                    generateViewModel.populateFromPrompt(
                                        promptText = artwork.prompt,
                                        negPrompt = artwork.negativePrompt,
                                        style = artwork.style,
                                        ratio = artwork.aspectRatio,
                                        qual = artwork.quality
                                    )
                                    navController.navigate(Screen.GENERATE) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }

                        // F: Dynamic Parameters settings preferences
                        composable(Screen.SETTINGS) {
                            SettingsScreen(viewModel = settingsViewModel)
                        }

                        // G: Full-Detailed Zoom Screen taking Long Argument ID
                        composable(
                            route = Screen.PREVIEW,
                            arguments = listOf(navArgument("id") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: 0L
                            PreviewScreen(
                                viewModel = previewViewModel,
                                artworkId = id,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val title: String,
    val emoji: String,
    val route: String,
    val testTag: String
)
