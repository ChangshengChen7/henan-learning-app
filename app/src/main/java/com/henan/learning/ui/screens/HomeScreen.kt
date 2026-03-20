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
fun HomeScreen(
    stats: StudyStats,
    todayReviewCount: Int,
    onNavigateToLearning: () -> Unit,
    onNavigateToProgress: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("河南省学习App") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { StatsCard(stats) }
            item { TodayReviewCard(todayReviewCount, onNavigateToLearning) }
            item { QuickActionsCard(onNavigateToLearning, onNavigateToProgress) }
        }
    }
}

@Composable
private fun StatsCard(stats: StudyStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("学习统计", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem("总知识点", stats.totalKnowledgePoints.toString(), InfoBlue)
                StatItem("已掌握", stats.masteredCount.toString(), SuccessGreen)
                StatItem("学习中", stats.learningCount.toString(), WarningOrange)
                StatItem("待学习", stats.pendingCount.toString(), TextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = stats.masteredPercentage / 100f,
                modifier = Modifier.fillMaxWidth(),
                color = SuccessGreen
            )
            Text("掌握进度: ${stats.masteredPercentage.toInt()}%", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun TodayReviewCard(count: Int, onStartReview: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.1f))) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("今日待复习", fontWeight = FontWeight.Bold)
                Text(if (count > 0) "还有 ${count} 个知识点等待复习" else "今日复习已完成！", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            if (count > 0) { FilledTonalButton(onClick = onStartReview) { Text("开始复习") } }
        }
    }
}

@Composable
private fun QuickActionsCard(onStartLearning: () -> Unit, onCheckProgress: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("快捷操作", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ActionButton(Icons.Default.PlayArrow, "开始学习", PrimaryGreen, onStartLearning)
                ActionButton(Icons.Default.BarChart, "查看进度", InfoBlue, onCheckProgress)
                ActionButton(Icons.Default.Share, "分享", PrimaryGold, { })
            }
        }
    }
}

@Composable
private fun ActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(onClick = onClick, colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = color.copy(alpha = 0.1f), contentColor = color)) { Icon(icon, contentDescription = label) }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}