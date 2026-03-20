package com.henan.learning.data.repository

import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.DailyPush
import com.henan.learning.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class KnowledgeRepository(private val dataStore: SimpleDataStore) {
    
    fun getAllKnowledgePoints(): Flow<List<KnowledgePoint>> =
        dataStore.knowledgePoints

    fun getKnowledgePointsByCategory(category: String): Flow<List<KnowledgePoint>> =
        dataStore.knowledgePoints.map { points ->
            points.filter { it.category == category }
        }

    suspend fun getKnowledgePointById(id: Int): KnowledgePoint? =
        dataStore.getKnowledgePointById(id.toLong())

    suspend fun insertKnowledgePoints(kps: List<KnowledgePoint>) =
        dataStore.insertKnowledgePoints(kps)

    suspend fun getKnowledgePointCount(): Int =
        dataStore.knowledgePoints.value.size
}

class ProgressRepository(private val dataStore: SimpleDataStore) {
    
    // 根据艾宾浩斯遗忘曲线计算下次复习时间
    fun calculateNextReview(reviewCount: Int): Long {
        val intervals = listOf(1, 3, 7, 14, 30, 60) // 天数
        val days = intervals.getOrElse(reviewCount) { 60 }
        return System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000L)
    }

    suspend fun startLearning(kpId: Int) {
        val progress = LearningProgress(
            knowledgePointId = kpId,
            status = "learning",
            reviewCount = 0,
            lastReviewAt = System.currentTimeMillis(),
            nextReviewAt = calculateNextReview(0)
        )
        dataStore.insertLearningProgress(progress)
    }

    suspend fun markAsMastered(kpId: Int) {
        val current = dataStore.getLearningProgressByPoint(kpId.toLong())
        if (current != null) {
            dataStore.insertLearningProgress(current.copy(status = "mastered"))
        }
    }

    suspend fun review(kpId: Int, mastered: Boolean) {
        if (mastered) {
            markAsMastered(kpId)
            return
        }

        val current = dataStore.getLearningProgressByPoint(kpId.toLong())
        if (current == null) {
            startLearning(kpId)
            return
        }

        val newReviewCount = current.reviewCount + 1
        val updated = current.copy(
            reviewCount = newReviewCount,
            lastReviewAt = System.currentTimeMillis(),
            nextReviewAt = calculateNextReview(newReviewCount)
        )
        dataStore.insertLearningProgress(updated)
    }

    suspend fun getProgressByKpId(kpId: Int): LearningProgress? =
        dataStore.getLearningProgressByPoint(kpId.toLong())

    fun getDueForReview(): Flow<List<LearningProgress>> =
        dataStore.learningProgress.map { progressList ->
            val now = System.currentTimeMillis()
            progressList.filter { it.nextReviewAt <= now }
        }

    fun getAllProgress(): Flow<List<LearningProgress>> =
        dataStore.learningProgress

    suspend fun getMasteredCount(): Int =
        dataStore.learningProgress.value.count { it.status == "mastered" }

    suspend fun getLearningCount(): Int =
        dataStore.learningProgress.value.count { it.status == "learning" }

    suspend fun getPendingCount(): Int =
        dataStore.learningProgress.value.count { it.status == "pending" }
}

class PushRepository(private val dataStore: SimpleDataStore) {
    
    fun getPendingPushes(): Flow<List<DailyPush>> =
        dataStore.dailyPushes.map { pushes ->
            pushes.filter { !it.isPushed }
        }

    suspend fun schedulePush(kpId: Int, pushTime: Long) {
        val push = DailyPush(
            knowledgePointId = kpId,
            pushTime = pushTime,
            isPushed = false
        )
        dataStore.insertDailyPush(push)
    }

    suspend fun markAsPushed(pushId: Int) {
        // 简化实现
    }

    suspend fun getPushesForToday(): List<DailyPush> {
        return dataStore.dailyPushes.value
    }
}

class SettingsRepository(private val dataStore: SimpleDataStore) {
    
    suspend fun getSetting(key: String): String? {
        val settings = dataStore.getSettings()
        return when (key) {
            "dailyPushEnabled" -> settings.dailyPushEnabled.toString()
            "pushTime" -> settings.pushTime
            "theme" -> settings.theme
            else -> null
        }
    }

    suspend fun setSetting(key: String, value: String) {
        val current = dataStore.getSettings()
        val updated = when (key) {
            "dailyPushEnabled" -> current.copy(dailyPushEnabled = value.toBoolean())
            "pushTime" -> current.copy(pushTime = value)
            "theme" -> current.copy(theme = value)
            else -> current
        }
        dataStore.updateSettings(updated)
    }

    suspend fun deleteSetting(key: String) {
        // 简化实现
    }
}