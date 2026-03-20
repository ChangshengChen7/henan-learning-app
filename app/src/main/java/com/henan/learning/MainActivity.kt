package com.henan.learning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.henan.learning.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as HenanLearningApp
        
        setContent {
            MaterialTheme {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(
                        app.knowledgeRepository,
                        app.progressRepository
                    )
                )
                
                val learningViewModel: LearningViewModel = viewModel(
                    factory = LearningViewModelFactory(
                        app.knowledgeRepository,
                        app.progressRepository
                    )
                )
                
                val progressViewModel: ProgressViewModel = viewModel(
                    factory = ProgressViewModelFactory(
                        app.knowledgeRepository,
                        app.progressRepository
                    )
                )
                
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(app.settingsRepository)
                )
                
                var selectedRoute by remember { mutableStateOf("home") }
                
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                                label = { Text("首页") },
                                selected = selectedRoute == "home",
                                onClick = { selectedRoute = "home" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.School, contentDescription = "学习") },
                                label = { Text("学习") },
                                selected = selectedRoute == "learning",
                                onClick = { selectedRoute = "learning" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.TrendingUp, contentDescription = "进度") },
                                label = { Text("进度") },
                                selected = selectedRoute == "progress",
                                onClick = { selectedRoute = "progress" }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Settings, contentDescription = "设置") },
                                label = { Text("设置") },
                                selected = selectedRoute == "settings",
                                onClick = { selectedRoute = "settings" }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedRoute) {
                            "home" -> HomeScreen(
                                viewModel = homeViewModel,
                                onNavigateToLearning = { selectedRoute = "learning" },
                                onNavigateToProgress = { selectedRoute = "progress" }
                            )
                            "learning" -> LearningScreen(
                                viewModel = learningViewModel,
                                onNavigateToProgress = { selectedRoute = "progress" }
                            )
                            "progress" -> ProgressScreen(viewModel = progressViewModel)
                            "settings" -> SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}
