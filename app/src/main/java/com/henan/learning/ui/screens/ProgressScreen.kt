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
                title = { Text("📈 学习进度") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProgressOverviewCard(stats)
            }

            item {
                ProgressChartCard(stats)
            }

            item {
                AchievementsCard(stats)
            }
        }
    }
}

@Composable
private fun ProgressOverviewCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📊 总体进度",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                progress = { stats.masteredPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp),
                strokeWidth = 12.dp,
                color = SuccessGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Text(
                "${stats.masteredPercentage.toInt()}% 掌握度",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun ProgressChartCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "📈 分类统计",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
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
private fun ProgressBarItem(
    label: String,
    value: Int,
    total: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            Text("$value", fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { if (total > 0) value.toFloat() / total else 0f },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun AchievementsCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "🏆 成就",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AchievementItem(
                    icon = Icons.EmojiEvents,
                    title = "初学者",
                    desc = "开始学习",
                    unlocked = stats.learningCount > 0
                )
                AchievementItem(
                    icon = Icons.Star,
                    title = "知识家",
                    desc = "掌握10个",
                    unlocked = stats.masteredCount >= 10
                )
                AchievementItem(
                    icon = Icons.WorkspacePremium,
                    title = "大师",
                    desc = "掌握50个",
                    unlocked = stats.masteredCount >= 50
                )
            }
        }
    }
}

@Composable
private fun AchievementItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    unlocked: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            contentDescription = title,
            modifier = Modifier.size(40.dp),
            tint = if (unlocked) PrimaryGold else TextSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = if (unlocked) TextPrimary else TextSecondary
        )
        Text(
            desc,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
