package com.henan.learning.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.domain.model.Difficulty
import com.henan.learning.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(
    knowledgePoints: List<KnowledgePoint>,
    selectedCategory: String?,
    onCategorySelect: (String?) -> Unit,
    onKnowledgeClick: (KnowledgePoint) -> Unit,
    onReview: (Int, Boolean) -> Unit
) {
    val categories = knowledgePoints.map { it.category }.distinct()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 学习中心") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 分类筛选
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onCategorySelect(null) },
                        label = { Text("全部") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelect(category) },
                        label = { Text(category) }
                    )
                }
            }

            // 知识点列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(knowledgePoints, key = { it.id }) { kp ->
                    KnowledgePointCard(
                        knowledgePoint = kp,
                        onClick = { onKnowledgeClick(kp) },
                        onReview = onReview
                    )
                }
            }
        }
    }
}

@Composable
private fun KnowledgePointCard(
    knowledgePoint: KnowledgePoint,
    onClick: () -> Unit,
    onReview: (Int, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        knowledgePoint.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = { },
                            label = { Text(knowledgePoint.category) },
                            leadingIcon = {
                                Icon(
                                    Icons.Category,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                        DifficultyChip(knowledgePoint.difficulty)
                    }
                }
                Icon(
                    if (expanded) Icons.ExpandLess else Icons.ExpandMore,
                    contentDescription = "展开"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    knowledgePoint.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = { onReview(knowledgePoint.id, false) }
                    ) {
                        Text("继续学习")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onReview(knowledgePoint.id, true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        )
                    ) {
                        Text("已掌握 ✓")
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: String) {
    val difficultyEnum = Difficulty.fromString(difficulty)
    val color = when (difficultyEnum) {
        Difficulty.EASY -> DifficultyEasy
        Difficulty.MEDIUM -> DifficultyMedium
        Difficulty.HARD -> DifficultyHard
    }

    AssistChip(
        onClick = { },
        label = { Text(difficultyEnum.toDisplayName()) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.2f),
            labelColor = color
        )
    )
}
