package com.henan.learning.data.model

data class KnowledgePoint(
    val id: Int = 0,
    val title: String,
    val content: String,
    val category: String = "other",
    val difficulty: String = "medium",
    val region: String = "河南省",
    val createdAt: Long = System.currentTimeMillis()
)

data class LearningProgress(
    val id: Int = 0,
    val knowledgePointId: Int,
    val status: String = "pending", // pending, learning, mastered
    val reviewCount: Int = 0,
    val lastReviewAt: Long? = null,
    val nextReviewAt: Long? = null
)

data class DailyPush(
    val id: Int = 0,
    val knowledgePointId: Int,
    val pushTime: Long,
    val isPushed: Boolean = false,
    val date: String = ""
)

data class UserSettings(
    val id: Int = 1,
    val dailyPushEnabled: Boolean = true,
    val pushTime: String = "08:00",
    val theme: String = "light"
)