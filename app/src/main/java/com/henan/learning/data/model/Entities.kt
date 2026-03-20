package com.henan.learning.data.model

/**
 * 知识点（红莲设计）
 * 
 * 重要区分：
 * - difficulty = 难度（知识点属性，不会变）
 * - status（在 LearningProgress 中）= 掌握状态（会变化）
 */
data class KnowledgePoint(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val difficulty: String  // "easy" | "medium" | "hard"
)

/**
 * 学习进度（红莲设计）
 * 
 * status 取值：
 * - "pending" = 未学习
 * - "learning" = 学习中
 * - "mastered" = 已掌握
 */
data class LearningProgress(
    val knowledgePointId: Int,
    val status: String,  // "pending" | "learning" | "mastered"
    val reviewCount: Int,
    val lastReviewAt: Long?
)