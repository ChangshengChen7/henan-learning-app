package com.henan.learning.data.backup

/**
 * 备份数据版本
 */
const val BACKUP_VERSION = "1.0"

/**
 * 备份数据根对象
 */
data class BackupData(
    val version: String = BACKUP_VERSION,
    val exportTime: String,
    val settings: BackupSettings,
    val progress: List<BackupProgress>
)

/**
 * 备份的设置数据
 */
data class BackupSettings(
    val dailyPushEnabled: Boolean,
    val pushTime: String,
    val pushCount: Int
)

/**
 * 备份的学习进度
 */
data class BackupProgress(
    val knowledgePointId: Int,
    val status: String,
    val reviewCount: Int
)
