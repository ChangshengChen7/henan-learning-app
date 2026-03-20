package com.henan.learning.domain.usecase

import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository
import com.henan.learning.ui.screens.StudyStats
import kotlinx.coroutines.flow.first

class GetStudyStatsUseCase(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(): StudyStats {
        val points = knowledgeRepository.getKnowledgePoints().first()
        val progress = progressRepository.getProgress().first()
        
        val learnedCount = progress.count { it.status == "learning" || it.status == "mastered" }
        val masteredCount = progress.count { it.status == "mastered" }
        
        return StudyStats(
            totalLearned = learnedCount,
            totalMastered = masteredCount,
            totalPoints = points.size
        )
    }
}
