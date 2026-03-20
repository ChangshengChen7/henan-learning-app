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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository
import com.henan.learning.data.repository.SettingsRepository
import com.henan.learning.domain.usecase.GetKnowledgePointsUseCase
import com.henan.learning.domain.usecase.GetStudyStatsUseCase
import com.henan.learning.domain.usecase.UpdateProgressUseCase
import com.henan.learning.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as HenanLearningApp
        
        setContent {
            MaterialTheme {
                val knowledgeRepository = KnowledgeRepository(app.dataStore)
                val progressRepository = ProgressRepository(app.dataStore)
                val settingsRepository = app.settingsRepository
                
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModelFactory(
                        GetKnowledgePointsUseCase(knowledgeRepository),
                        GetStudyStatsUseCase(knowledgeRepository, progressRepository)
                    )
                )
                
                val learningViewModel: LearningViewModel = viewModel(
                    factory = LearningViewModelFactory(
                        GetKnowledgePointsUseCase(knowledgeRepository),
                        UpdateProgressUseCase(progressRepository)
                    )
                )
                
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = SettingsViewModelFactory(settingsRepository)
                )
                
                var selectedRoute by remember { mutableStateOf("home") }
                
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationItem(
                                route = "home",
                                icon = Icons.Default.Home,
                                label = "首页",
                                selected = selectedRoute == "home",
                                onClick = { selectedRoute = "home" }
                            )
                            NavigationItem(
                                route = "learning",
                                icon = Icons.Default.School,
                                label = "学习",
                                selected = selectedRoute == "learning",
                                onClick = { selectedRoute = "learning" }
                            )
                            NavigationItem(
                                route = "progress",
                                icon = Icons.Default.TrendingUp,
                                label = "进度",
                                selected = selectedRoute == "progress",
                                onClick = { selectedRoute = "progress" }
                            )
                            NavigationItem(
                                route = "settings",
                                icon = Icons.Default.Settings,
                                label = "设置",
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
                            "progress" -> ProgressScreen(
                                viewModel = homeViewModel,
                                onNavigateToLearning = { selectedRoute = "learning" }
                            )
                            "settings" -> SettingsScreen(viewModel = settingsViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationItem(
    route: String,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick
    )
}
