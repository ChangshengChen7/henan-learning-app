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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val settings by viewModel.settings.collectAsState()
    
    var showTimePicker by remember { mutableStateOf(false) }
    var showNumberPicker by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("每日推送") },
                    supportingContent = { Text("每天定时推送知识点") },
                    leadingContent = { Icon(Icons.Default.Notifications, null) },
                    trailingContent = {
                        Switch(
                            checked = settings.dailyPushEnabled,
                            onCheckedChange = { viewModel.updateDailyPushEnabled(it) }
                        )
                    }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("推送时间") },
                    supportingContent = { Text(settings.pushTime) },
                    leadingContent = { Icon(Icons.Default.Timer, null) },
                    modifier = Modifier.clickable { showTimePicker = true }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("每日推送数量") },
                    supportingContent = { Text("${settings.dailyPushCount} 个知识点") },
                    leadingContent = { Icon(Icons.Default.Category, null) },
                    modifier = Modifier.clickable { showNumberPicker = true }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("艾宾浩斯复习") },
                    supportingContent = { Text("自动根据遗忘曲线安排复习") },
                    leadingContent = { Icon(Icons.Default.AutoAwesome, null) },
                    trailingContent = {
                        Switch(
                            checked = settings.ebbinghausEnabled,
                            onCheckedChange = { viewModel.updateEbbinghausEnabled(it) }
                        )
                    }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("数据备份") },
                    supportingContent = { Text("将学习数据备份到云端") },
                    leadingContent = { Icon(Icons.Default.Backup, null) }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { Text("数据恢复") },
                    supportingContent = { Text("从备份恢复学习数据") },
                    leadingContent = { Icon(Icons.Default.Restore, null) }
                )
            }
            
            item {
                ListItem(
                    headlineContent = { 
                        Text("清除数据", color = MaterialTheme.colorScheme.error) 
                    },
                    supportingContent = { Text("清除所有学习记录") },
                    leadingContent = { 
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) 
                    },
                    modifier = Modifier.clickable { showClearDataDialog = true }
                )
            }
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initialHour = 8,
            initialMinute = 0,
            onTimeSelected = { hour, minute -> viewModel.updatePushTime(hour, minute) },
            onDismiss = { showTimePicker = false }
        )
    }

    if (showNumberPicker) {
        NumberPickerDialog(
            initialValue = settings.dailyPushCount,
            range = 1..10,
            onValueSelected = { value -> viewModel.updateDailyPushCount(value) },
            onDismiss = { showNumberPicker = false }
        )
    }

    if (showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { showClearDataDialog = false },
            title = { Text("清除所有学习记录？") },
            text = { Text("此操作不可恢复，所有学习进度将被清空。") },
            confirmButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("确定", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
                onDismiss()
            }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
        text = { TimePicker(state = timePickerState) }
    )
}

@Composable
fun NumberPickerDialog(
    initialValue: Int,
    range: IntRange,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedValue by remember { mutableIntStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onValueSelected(selectedValue)
                onDismiss()
            }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("选择数量", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("$selectedValue", style = MaterialTheme.typography.displayMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    TextButton(onClick = { if (selectedValue > range.first) selectedValue-- }) {
                        Text("-")
                    }
                    TextButton(onClick = { if (selectedValue < range.last) selectedValue++ }) {
                        Text("+")
                    }
                }
            }
        }
    )
}
