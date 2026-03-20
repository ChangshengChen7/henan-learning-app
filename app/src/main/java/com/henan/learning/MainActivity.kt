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
import com.henan.learning.ui.screens.*
import com.henan.learning.ui.theme.HenanLearningTheme

/**
 * MainActivity（红莲设计）
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as HenanLearningApp

        setContent {
            HenanLearningTheme {
                MainScreen(app = app)
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "首页", Icons.Filled.Home)
    object Learning : Screen("learning", "学习", Icons.Filled.Book)
    object Progress : Screen("progress", "进度", Icons.Filled.BarChart)
    object Settings : Screen("settings", "设置", Icons.Filled.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(app: HenanLearningApp) {
    var selectedRoute by remember { mutableStateOf("home") }

    // 创建 ViewModel（直接使用 dataStore）
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(app.dataStore)
    )

    val learningViewModel: LearningViewModel = viewModel(
        factory = LearningViewModelFactory(app.dataStore)
    )

    val progressViewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModelFactory(app.dataStore)
    )

    val items = listOf(
        Screen.Home,
        Screen.Learning,
        Screen.Progress,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedRoute == screen.route,
                        onClick = { selectedRoute = screen.route }
                    )
                }
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
                    viewModel = progressViewModel
                )
                "settings" -> SettingsScreen()
            }
        }
    }
}