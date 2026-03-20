package com.henan.learning.domain.usecase

import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.repository.ProgressRepository

class UpdateProgressUseCase(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(knowledgePointId: Int, status: String) {
        val progress = LearningProgress(
            knowledgePointId = knowledgePointId,
            status = status,
            reviewCount = 1,
            lastReviewAt = System.currentTimeMillis()
        )
        progressRepository.updateProgress(progress)
    }
}
