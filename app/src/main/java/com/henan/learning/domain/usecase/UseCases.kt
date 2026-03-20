package com.henan.learning.domain.usecase

import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository
import com.henan.learning.data.repository.PushRepository
import com.henan.learning.domain.model.StudyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetKnowledgePointsUseCase(
    private val knowledgeRepository: KnowledgeRepository
) {
    operator fun invoke(): Flow<List<KnowledgePoint>> =
        knowledgeRepository.getAllKnowledgePoints()

    fun byCategory(category: String): Flow<List<KnowledgePoint>> =
        knowledgeRepository.getKnowledgePointsByCategory(category)
}

class GetStudyStatsUseCase(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(): StudyStats {
        val total = knowledgeRepository.getKnowledgePointCount()
        val mastered = progressRepository.getMasteredCount()
        val learning = progressRepository.getLearningCount()
        val pending = total - mastered - learning

        return StudyStats(
            totalKnowledgePoints = total,
            masteredCount = mastered,
            learningCount = learning,
            pendingCount = pending.coerceAtLeast(0)
        )
    }
}

class ReviewKnowledgeUseCase(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(kpId: Int, mastered: Boolean) {
        progressRepository.review(kpId, mastered)
    }

    suspend fun startLearning(kpId: Int) {
        progressRepository.startLearning(kpId)
    }
}

class ScheduleDailyPushUseCase(
    private val pushRepository: PushRepository
) {
    suspend operator fun invoke(kpIds: List<Int>, hour: Int = 8) {
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
        }
        val pushTime = calendar.timeInMillis

        kpIds.forEach { kpId ->
            pushRepository.schedulePush(kpId, pushTime)
        }
    }
}

class ImportKnowledgePointsUseCase(
    private val knowledgeRepository: KnowledgeRepository
) {
    suspend operator fun invoke(kps: List<KnowledgePoint>) {
        knowledgeRepository.insertKnowledgePoints(kps)
    }
}
