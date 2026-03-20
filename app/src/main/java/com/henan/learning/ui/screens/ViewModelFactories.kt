package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.henan.learning.data.repository.SettingsRepository

class HomeViewModelFactory(
    private val getKnowledgePointsUseCase: com.henan.learning.domain.usecase.GetKnowledgePointsUseCase,
    private val getStudyStatsUseCase: com.henan.learning.domain.usecase.GetStudyStatsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(getKnowledgePointsUseCase, getStudyStatsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LearningViewModelFactory(
    private val getKnowledgePointsUseCase: com.henan.learning.domain.usecase.GetKnowledgePointsUseCase,
    private val updateProgressUseCase: com.henan.learning.domain.usecase.UpdateProgressUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            return LearningViewModel(getKnowledgePointsUseCase, updateProgressUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class ProgressViewModelFactory(
    private val getKnowledgePointsUseCase: GetKnowledgePointsUseCase,
    private val getStudyStatsUseCase: GetStudyStatsUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            return ProgressViewModel(getKnowledgePointsUseCase, getStudyStatsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
