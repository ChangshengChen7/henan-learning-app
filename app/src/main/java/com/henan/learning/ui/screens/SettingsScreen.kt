package com.henan.learning.ui.screens

import androidx.compose.foundation.clickable
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
import com.henan.learning.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var pushEnabled by remember { mutableStateOf(true) }
    var pushTime by remember { mutableStateOf("08:00") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
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
        ) {
            item {
                SettingsSection("推送设置") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Notifications,
                        title = "每日推送",
                        subtitle = "每天定时推送知识点",
                        checked = pushEnabled,
                        onCheckedChange = { pushEnabled = it }
                    )
                    SettingsClickItem(
                        icon = Icons.Default.Timer,
                        title = "推送时间",
                        subtitle = pushTime,
                        onClick = { }
                    )
                }
            }

            item {
                SettingsSection("学习设置") {
                    SettingsSwitchItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "艾宾浩斯复习",
                        subtitle = "自动根据遗忘曲线安排复习",
                        checked = true,
                        onCheckedChange = { }
                    )
                    SettingsClickItem(
                        icon = Icons.Default.Category,
                        title = "每日推送数量",
                        subtitle = "5 个知识点",
                        onClick = { }
                    )
                }
            }

            item {
                SettingsSection("数据管理") {
                    SettingsClickItem(
                        icon = Icons.Default.Backup,
                        title = "数据备份",
                        subtitle = "将学习数据备份到云端",
                        onClick = { }
                    )
                    SettingsClickItem(
                        icon = Icons.Default.Restore,
                        title = "数据恢复",
                        subtitle = "从备份恢复学习数据",
                        onClick = { }
                    )
                    SettingsClickItem(
                        icon = Icons.Default.Delete,
                        title = "清除数据",
                        subtitle = "清除所有学习记录",
                        onClick = { },
                        isDestructive = true
                    )
                }
            }

            item {
                SettingsSection("关于") {
                    SettingsClickItem(
                        icon = Icons.Default.Info,
                        title = "版本信息",
                        subtitle = "V1.0.0",
                        onClick = { }
                    )
                    SettingsClickItem(
                        icon = Icons.Default.Description,
                        title = "使用说明",
                        subtitle = "了解如何使用本应用",
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen)
            Column {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsClickItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isDestructive) ErrorRed else PrimaryGreen
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDestructive) ErrorRed else TextPrimary
            )
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = TextSecondary
        )
    }
}