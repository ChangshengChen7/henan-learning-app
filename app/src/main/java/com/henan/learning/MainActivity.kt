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
import com.henan.learning.ui.screens.*
import com.henan.learning.ui.theme.HenanLearningTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as HenanLearningApp

        setContent {
            HenanLearningTheme {
                MainScreen(app)
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
                    stats = com.henan.learning.domain.model.StudyStats(
                        totalKnowledgePoints = 44,
                        masteredCount = 12,
                        learningCount = 20,
                        pendingCount = 12
                    ),
                    todayReviewCount = 5,
                    onNavigateToLearning = { selectedRoute = "learning" },
                    onNavigateToProgress = { selectedRoute = "progress" }
                )
                "learning" -> LearningScreen(
                    knowledgePoints = emptyList(),
                    selectedCategory = null,
                    onCategorySelect = { },
                    onKnowledgeClick = { },
                    onReview = { _, _ -> }
                )
                "progress" -> ProgressScreen(
                    stats = com.henan.learning.domain.model.StudyStats(
                        totalKnowledgePoints = 44,
                        masteredCount = 12,
                        learningCount = 20,
                        pendingCount = 12
                    )
                )
                "settings" -> SettingsScreen()
            }
        }
    }
}
