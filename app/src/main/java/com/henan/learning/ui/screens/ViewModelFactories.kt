package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.henan.learning.data.repository.SettingsRepository
import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository

class HomeViewModelFactory(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(knowledgeRepository, progressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LearningViewModelFactory(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LearningViewModel::class.java)) {
            return LearningViewModel(knowledgeRepository, progressRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProgressViewModelFactory(
    private val knowledgeRepository: KnowledgeRepository,
    private val progressRepository: ProgressRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            return ProgressViewModel(knowledgeRepository, progressRepository) as T
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