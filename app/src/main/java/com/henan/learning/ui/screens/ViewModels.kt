package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.KnowledgePoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ============ HomeViewModel ============

data class HomeUiState(
    val totalKnowledgePoints: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val pendingCount: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val dataStore: SimpleDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            // 合并知识点和学习进度
            dataStore.knowledgePoints.combine(dataStore.learningProgress) { points, progress ->
                val total = points.size
                val mastered = progress.count { it.status == "mastered" }
                val learning = progress.count { it.status == "learning" }
                val pending = total - mastered - learning
                
                HomeUiState(
                    totalKnowledgePoints = total,
                    masteredCount = mastered,
                    learningCount = learning,
                    pendingCount = pending.coerceAtLeast(0),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

// ============ LearningViewModel ============

data class LearningUiState(
    val knowledgePoints: List<KnowledgePoint> = emptyList(),
    val selectedCategory: String? = null,
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = true
)

class LearningViewModel(
    private val dataStore: SimpleDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(LearningUiState())
    val uiState: StateFlow<LearningUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            dataStore.knowledgePoints.collect { points ->
                val categories = points.map { it.category }.distinct()
                _uiState.update { 
                    it.copy(
                        knowledgePoints = points,
                        categories = categories,
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun selectCategory(category: String?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }
    
    fun startLearning(kpId: Int) {
        dataStore.startLearning(kpId)
    }
    
    fun markAsMastered(kpId: Int) {
        dataStore.markAsMastered(kpId)
    }
    
    fun getStatus(kpId: Int): String {
        return dataStore.getStatus(kpId)
    }
    
    fun getFilteredPoints(): List<KnowledgePoint> {
        val state = _uiState.value
        return if (state.selectedCategory != null) {
            state.knowledgePoints.filter { it.category == state.selectedCategory }
        } else {
            state.knowledgePoints
        }
    }
}

// ============ ProgressViewModel ============

data class ProgressUiState(
    val totalKnowledgePoints: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val pendingCount: Int = 0,
    val isLoading: Boolean = true
)

class ProgressViewModel(
    private val dataStore: SimpleDataStore
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            dataStore.knowledgePoints.combine(dataStore.learningProgress) { points, progress ->
                val total = points.size
                val mastered = progress.count { it.status == "mastered" }
                val learning = progress.count { it.status == "learning" }
                val pending = total - mastered - learning
                
                ProgressUiState(
                    totalKnowledgePoints = total,
                    masteredCount = mastered,
                    learningCount = learning,
                    pendingCount = pending.coerceAtLeast(0),
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}