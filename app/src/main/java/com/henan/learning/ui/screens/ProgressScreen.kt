package com.henan.learning.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.henan.learning.domain.model.StudyStats
import com.henan.learning.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(stats: StudyStats) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("学习进度") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { ProgressOverviewCard(stats) }
            item { ProgressChartCard(stats) }
            item { AchievementsCard(stats) }
        }
    }
}

@Composable
private fun ProgressOverviewCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("总体进度", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(progress = { stats.masteredPercentage / 100f }, modifier = Modifier.size(120.dp), strokeWidth = 12.dp, color = SuccessGreen)
            Spacer(modifier = Modifier.height(16.dp))
            Text("${stats.masteredPercentage.toInt()}% 掌握度", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ProgressChartCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("分类统计", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            ProgressBarItem("已掌握", stats.masteredCount, stats.totalKnowledgePoints, SuccessGreen)
            Spacer(modifier = Modifier.height(8.dp))
            ProgressBarItem("学习中", stats.learningCount, stats.totalKnowledgePoints, WarningOrange)
            Spacer(modifier = Modifier.height(8.dp))
            ProgressBarItem("待学习", stats.pendingCount, stats.totalKnowledgePoints, TextSecondary)
        }
    }
}

@Composable
private fun ProgressBarItem(label: String, value: Int, total: Int, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$value", fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(progress = { if (total > 0) value.toFloat() / total else 0f }, modifier = Modifier.fillMaxWidth(), color = color)
    }
}

@Composable
private fun AchievementsCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("成就", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                AchievementItem(Icons.Default.EmojiEvents, "初学者", "开始学习", stats.learningCount > 0)
                AchievementItem(Icons.Default.Star, "知识家", "掌握10个", stats.masteredCount >= 10)
                AchievementItem(Icons.Default.WorkspacePremium, "大师", "掌握50个", stats.masteredCount >= 50)
            }
        }
    }
}

@Composable
private fun AchievementItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, desc: String, unlocked: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = title, modifier = Modifier.size(40.dp), tint = if (unlocked) PrimaryGold else TextSecondary.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (unlocked) TextPrimary else TextSecondary)
        Text(desc, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}