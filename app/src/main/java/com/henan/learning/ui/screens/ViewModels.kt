package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.domain.model.StudyStats
import com.henan.learning.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val stats: StudyStats = StudyStats(),
    val todayReviewCount: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val getStudyStatsUseCase: GetStudyStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val stats = getStudyStatsUseCase()

            _uiState.update {
                it.copy(
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }
}

data class LearningUiState(
    val knowledgePoints: List<KnowledgePoint> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = true
)

class LearningViewModel(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val reviewKnowledgeUseCase: ReviewKnowledgeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()

    init {
        loadKnowledgePoints()
    }

    fun loadKnowledgePoints(category: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category) }

            val flow = if (category != null) {
                getKnowledgePointsUseCase.byCategory(category)
            } else {
                getKnowledgePointsUseCase()
            }

            flow.collect { kps ->
                _uiState.update {
                    it.copy(
                        knowledgePoints = kps,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun review(kpId: Int, mastered: Boolean) {
        viewModelScope.launch {
            reviewKnowledgeUseCase(kpId, mastered)
        }
    }
}

data class ProgressUiState(
    val stats: StudyStats = StudyStats(),
    val dueForReview: List<LearningProgress> = emptyList(),
    val isLoading: Boolean = true
)

class ProgressViewModel(
    private val getStudyStatsUseCase: GetStudyStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val stats = getStudyStatsUseCase()

            _uiState.update {
                it.copy(
                    stats = stats,
                    isLoading = false
                )
            }
        }
    }
}
