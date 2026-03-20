package com.henan.learning.domain.usecase

import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.repository.KnowledgeRepository
import kotlinx.coroutines.flow.Flow

class GetKnowledgePointsUseCase(
    private val knowledgeRepository: KnowledgeRepository
) {
    operator fun invoke(): Flow<List<KnowledgePoint>> = knowledgeRepository.getKnowledgePoints()
}
