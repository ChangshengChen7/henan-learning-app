package com.henan.learning

import android.app.Application
import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.repository.KnowledgeRepository
import com.henan.learning.data.repository.ProgressRepository
import com.henan.learning.data.repository.SettingsRepository
import com.henan.learning.domain.usecase.GetKnowledgePointsUseCase
import com.henan.learning.domain.usecase.GetStudyStatsUseCase
import com.henan.learning.domain.usecase.UpdateProgressUseCase

class HenanLearningApp : Application() {
    
    lateinit var dataStore: SimpleDataStore
        private set
    
    lateinit var knowledgeRepository: KnowledgeRepository
        private set
    
    lateinit var progressRepository: ProgressRepository
        private set
    
    lateinit var settingsRepository: SettingsRepository
        private set
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化数据存储
        dataStore = SimpleDataStore(this)
        dataStore.loadKnowledgePointsFromAssets()
        
        // 初始化仓库
        knowledgeRepository = KnowledgeRepository(dataStore)
        progressRepository = ProgressRepository(dataStore)
        settingsRepository = SettingsRepository(dataStore)
    }
}
