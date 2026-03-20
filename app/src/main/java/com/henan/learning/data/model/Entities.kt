package com.henan.learning.data.model

/**
 * 知识点
 */
data class KnowledgePoint(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val difficulty: String = "medium"
)

/**
 * 学习进度
 */
data class LearningProgress(
    val knowledgePointId: Int,
    val status: String = "pending",  // pending, learning, mastered
    val reviewCount: Int = 0,
    val lastReviewAt: Long? = null
)

/**
 * 用户设置
 */
data class UserSettings(
    val id: Int = 1,
    val dailyPushEnabled: Boolean = true,
    val pushTime: String = "08:00",
    val dailyPushCount: Int = 5,
    val ebbinghausEnabled: Boolean = true,
    val theme: String = "light"
)

/**
 * 每日推送
 */
data class DailyPush(
    val id: Int,
    val knowledgePointId: Int,
    val pushDate: String,
    val isRead: Boolean = false
)
