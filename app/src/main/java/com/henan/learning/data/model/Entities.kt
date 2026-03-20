package com.henan.learning.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "knowledge_points")
data class KnowledgePoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "other",
    val difficulty: String = "medium",
    val region: String = "河南省",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_progress")
data class LearningProgress(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val knowledgePointId: Int,
    val status: String = "pending", // pending, learning, mastered
    val reviewCount: Int = 0,
    val lastReviewAt: Long? = null,
    val nextReviewAt: Long? = null
)

@Entity(tableName = "daily_push")
data class DailyPush(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val knowledgePointId: Int,
    val pushTime: Long,
    val isPushed: Boolean = false
)

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val key: String,
    val value: String
)
