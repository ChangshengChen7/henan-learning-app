package com.henan.learning.data.local

import android.content.Context
import com.henan.learning.data.model.*
import kotlinx.coroutines.flow.*

/**
 * 简化的数据存储 - 使用内存存储替代 Room
 */
class SimpleDataStore(context: Context) {
    
    private val _knowledgePoints = MutableStateFlow<List<KnowledgePoint>>(emptyList())
    val knowledgePoints: StateFlow<List<KnowledgePoint>> = _knowledgePoints.asStateFlow()
    
    private val _learningProgress = MutableStateFlow<List<LearningProgress>>(emptyList())
    val learningProgress: StateFlow<List<LearningProgress>> = _learningProgress.asStateFlow()
    
    private val _dailyPushes = MutableStateFlow<List<DailyPush>>(emptyList())
    val dailyPushes: StateFlow<List<DailyPush>> = _dailyPushes.asStateFlow()
    
    private val _userSettings = MutableStateFlow(
        UserSettings(
            id = 1,
            dailyPushEnabled = true,
            pushTime = "08:00",
            theme = "light"
        )
    )
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()
    
    // Knowledge Points
    suspend fun insertKnowledgePoints(points: List<KnowledgePoint>) {
        _knowledgePoints.value = points
    }
    
    suspend fun getAllKnowledgePoints(): List<KnowledgePoint> {
        return _knowledgePoints.value
    }
    
    suspend fun getKnowledgePointById(id: Long): KnowledgePoint? {
        return _knowledgePoints.value.find { it.id == id }
    }
    
    // Learning Progress
    suspend fun insertLearningProgress(progress: LearningProgress) {
        val current = _learningProgress.value.toMutableList()
        val index = current.indexOfFirst { it.knowledgePointId == progress.knowledgePointId }
        if (index >= 0) {
            current[index] = progress
        } else {
            current.add(progress)
        }
        _learningProgress.value = current
    }
    
    suspend fun getLearningProgressByPoint(pointId: Long): LearningProgress? {
        return _learningProgress.value.find { it.knowledgePointId == pointId }
    }
    
    // Daily Push
    suspend fun insertDailyPush(push: DailyPush) {
        val current = _dailyPushes.value.toMutableList()
        current.add(push)
        _dailyPushes.value = current
    }
    
    suspend fun getTodayPush(): DailyPush? {
        val today = java.time.LocalDate.now().toString()
        return _dailyPushes.value.find { it.date == today }
    }
    
    // User Settings
    suspend fun updateSettings(settings: UserSettings) {
        _userSettings.value = settings
    }
    
    suspend fun getSettings(): UserSettings {
        return _userSettings.value
    }
    
    companion object {
        @Volatile
        private var INSTANCE: SimpleDataStore? = null
        
        fun getInstance(context: Context): SimpleDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SimpleDataStore(context).also { INSTANCE = it }
            }
        }
    }
}