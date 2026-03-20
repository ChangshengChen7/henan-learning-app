package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.UserSettings
import com.henan.learning.data.repository.SettingsRepository
import com.henan.learning.domain.usecase.GetKnowledgePointsUseCase
import com.henan.learning.domain.usecase.GetStudyStatsUseCase
import com.henan.learning.domain.usecase.UpdateProgressUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Home ViewModel
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

class HomeViewModel(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val getStudyStatsUseCase: GetStudyStatsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = getKnowledgePointsUseCase().first()
                val stats = getStudyStatsUseCase()
                _uiState.update {
                    it.copy(
                        knowledgePoints = points,
                        stats = stats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

// Learning ViewModel
data class LearningUiState(
    val knowledgePoints: List<KnowledgePoint> = emptyList(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = false
)

class LearningViewModel(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val updateProgressUseCase: UpdateProgressUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = getKnowledgePointsUseCase().first()
                _uiState.update {
                    it.copy(knowledgePoints = points, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    fun markAsLearned(pointId: Int) {
        viewModelScope.launch {
            updateProgressUseCase(pointId, "learning")
        }
    }
    
    fun markAsMastered(pointId: Int) {
        viewModelScope.launch {
            updateProgressUseCase(pointId, "mastered")
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
        try {
            val s = settingsRepository.getSettings()
            _settings.value = s
            _uiState.update { it.copy(settings = s) }
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "加载设置失败: ${e.message}") }
        }
    }
    
    fun updateDailyPushEnabled(enabled: Boolean) {
        val newSettings = _settings.value.copy(dailyPushEnabled = enabled)
        saveSettings(newSettings)
    }
    
    fun updatePushTime(hour: Int, minute: Int) {
        val timeString = String.format("%02d:%02d", hour, minute)
        val newSettings = _settings.value.copy(pushTime = timeString)
        saveSettings(newSettings)
    }
    
    fun updateDailyPushCount(count: Int) {
        val newSettings = _settings.value.copy(dailyPushCount = count)
        saveSettings(newSettings)
    }
    
    fun updateEbbinghausEnabled(enabled: Boolean) {
        val newSettings = _settings.value.copy(ebbinghausEnabled = enabled)
        saveSettings(newSettings)
    }
    
    private fun saveSettings(settings: UserSettings) {
        try {
            settingsRepository.updateSettings(settings)
            _settings.value = settings
            _uiState.update { it.copy(settings = settings, message = "设置已保存") }
        } catch (e: Exception) {
            _uiState.update { it.copy(message = "保存设置失败: ${e.message}") }
        }
    }
    
    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }
}

// Progress ViewModel
class ProgressViewModel(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val getStudyStatsUseCase: GetStudyStatsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val points = getKnowledgePointsUseCase().first()
                val stats = getStudyStatsUseCase()
                _uiState.update {
                    it.copy(
                        knowledgePoints = points,
                        stats = stats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}
