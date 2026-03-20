package com.henan.learning.data.repository

import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepository(private val dataStore: SimpleDataStore) {
    suspend fun getSettings(): UserSettings = dataStore.getSettings()
    suspend fun updateSettings(settings: UserSettings) = dataStore.updateSettings(settings)
    fun getSettingsFlow(): Flow<UserSettings> = flowOf(dataStore.getSettings())
}
