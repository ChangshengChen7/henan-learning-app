package com.henan.learning.domain.model

data class StudyStats(
    val totalKnowledgePoints: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val pendingCount: Int = 0
) {
    val masteredPercentage: Float
        get() = if (totalKnowledgePoints > 0)
            (masteredCount.toFloat() / totalKnowledgePoints * 100) else 0f

    val learningPercentage: Float
        get() = if (totalKnowledgePoints > 0)
            (learningCount.toFloat() / totalKnowledgePoints * 100) else 0f
}

enum class Difficulty {
    EASY, MEDIUM, HARD;

    companion object {
        fun fromString(value: String): Difficulty {
            return when (value.lowercase()) {
                "easy" -> EASY
                "hard" -> HARD
                else -> MEDIUM
            }
        }
    }

    fun toDisplayName(): String {
        return when (this) {
            EASY -> "简单"
            MEDIUM -> "中等"
            HARD -> "困难"
        }
    }
}

data class CategoryCount(
    val category: String,
    val count: Int
)
