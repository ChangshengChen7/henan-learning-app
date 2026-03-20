package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.UserSettings
import com.henan.learning.data.repository.SettingsRepository
import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Home UiState
data class HomeUiState(
    val knowledgePoints: List<KnowledgePoint> = emptyList(),
    val stats: StudyStats = StudyStats(),
    val todayReviewCount: Int = 0,
    val isLoading: Boolean = false
)

data class StudyStats(
    val totalLearned: Int = 0,
    val totalMastered: Int = 0,
    val totalPoints: Int = 0
)

// Home ViewModel
class HomeViewModel(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = knowledgeRepository.getKnowledgePoints().first()
                val progress = progressRepository.getProgress().first()
                
                val learnedCount = progress.count { it.status == "learning" || it.status == "mastered" }
                val masteredCount = progress.count { it.status == "mastered" }
                
                _uiState.update {
                    it.copy(
                        knowledgePoints = points,
                        stats = StudyStats(
                            totalLearned = learnedCount,
                            totalMastered = masteredCount,
                            totalPoints = points.size
                        ),
                        todayReviewCount = points.size - masteredCount,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

// Learning UiState
data class LearningUiState(
    val knowledgePoints: List<KnowledgePoint> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String? = null,
    val progress: Map<Int, String> = emptyMap(),
    val isLoading: Boolean = false
)

// Learning ViewModel
class LearningViewModel(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = knowledgeRepository.getKnowledgePoints().first()
                val progress = progressRepository.getProgress().first()
                
                val categories = points.map { it.category }.distinct()
                val progressMap = progress.associate { it.knowledgePointId to it.status }
                
                _uiState.update {
                    it.copy(
                        knowledgePoints = points,
                        categories = categories,
                        progress = progressMap,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun getFilteredPoints(): List<KnowledgePoint> {
        val state = _uiState.value
        return if (state.selectedCategory == null) {
            state.knowledgePoints
        } else {
            state.knowledgePoints.filter { it.category == state.selectedCategory }
        }
    }
    
    fun getStatus(pointId: Int): String {
        return _uiState.value.progress[pointId] ?: "pending"
    }
    
    fun startLearning(pointId: Int) {
        viewModelScope.launch {
            progressRepository.updateProgress(
                LearningProgress(
                    knowledgePointId = pointId,
                    status = "learning",
                    reviewCount = 1,
                    lastReviewAt = System.currentTimeMillis()
                )
            )
            loadData()
        }
    }
    
    fun markAsMastered(pointId: Int) {
        viewModelScope.launch {
            progressRepository.updateProgress(
                LearningProgress(
                    knowledgePointId = pointId,
                    status = "mastered",
                    reviewCount = (_uiState.value.progress[pointId]?.let { 1 } ?: 0) + 1,
                    lastReviewAt = System.currentTimeMillis()
                )
            )
            loadData()
        }
    }
}

// Progress UiState
data class ProgressUiState(
    val totalKnowledgePoints: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val pendingCount: Int = 0,
    val categoryStats: List<CategoryStat> = emptyList(),
    val isLoading: Boolean = false
)

data class CategoryStat(
    val name: String,
    val total: Int,
    val mastered: Int
)

// Progress ViewModel
class ProgressViewModel(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = knowledgeRepository.getKnowledgePoints().first()
                val progress = progressRepository.getProgress().first()
                
                val progressMap = progress.associate { it.knowledgePointId to it.status }
                val masteredCount = progress.count { it.status == "mastered" }
                val learningCount = progress.count { it.status == "learning" }
                val pendingCount = points.size - masteredCount - learningCount
                
                // 按分类统计
                val categoryStats = points.groupBy { it.category }.map { (category, categoryPoints) ->
                    CategoryStat(
                        name = category,
                        total = categoryPoints.size,
                        mastered = categoryPoints.count { progressMap[it.id] == "mastered" }
                    )
                }
                
                _uiState.update {
                    it.copy(
                        totalKnowledgePoints = points.size,
                        masteredCount = masteredCount,
                        learningCount = learningCount,
                        pendingCount = pendingCount,
                        categoryStats = categoryStats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

// Settings ViewModel - 蔡文姬实现
data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val message: String? = null
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _settings = MutableStateFlow(UserSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val s = settingsRepository.getSettings()
                _settings.value = s
                _uiState.update { it.copy(settings = s) }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "加载设置失败: ${e.message}") }
            }
        }
    }
    
    fun updateDailyPushEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(dailyPushEnabled = enabled)
            saveSettings(newSettings)
        }
    }
    
    fun updatePushTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            val timeString = String.format("%02d:%02d", hour, minute)
            val newSettings = _settings.value.copy(pushTime = timeString)
            saveSettings(newSettings)
        }
    }
    
    fun updateDailyPushCount(count: Int) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(dailyPushCount = count)
            saveSettings(newSettings)
        }
    }
    
    fun updateEbbinghausEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val newSettings = _settings.value.copy(ebbinghausEnabled = enabled)
            saveSettings(newSettings)
        }
    }
    
    private fun saveSettings(settings: UserSettings) {
        viewModelScope.launch {
            try {
                settingsRepository.updateSettings(settings)
                _settings.value = settings
                _uiState.update { it.copy(settings = settings, message = "设置已保存") }
            } catch (e: Exception) {
                _uiState.update { it.copy(message = "保存设置失败: ${e.message}") }
            }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}