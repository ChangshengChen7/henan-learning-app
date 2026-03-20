package com.henan.learning.data.repository

import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import kotlinx.coroutines.flow.Flow

class KnowledgeRepository(private val dataStore: SimpleDataStore) {
    
    fun getKnowledgePoints(): Flow<List<KnowledgePoint>> = dataStore.knowledgePoints
}

class ProgressRepository(private val dataStore: SimpleDataStore) {
    
    fun getProgress(): Flow<List<LearningProgress>> = dataStore.learningProgress
    
    suspend fun updateProgress(progress: LearningProgress) {
        dataStore.updateProgress(progress)
    }
    
    suspend fun clearAllData() {
        dataStore.clearAllData()
    }
}