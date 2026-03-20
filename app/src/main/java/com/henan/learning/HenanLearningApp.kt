package com.henan.learning

import android.app.Application
import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.repository.*

class HenanLearningApp : Application() {

    lateinit var dataStore: SimpleDataStore
        private set

    lateinit var knowledgeRepository: KnowledgeRepository
        private set

    lateinit var progressRepository: ProgressRepository
        private set

    lateinit var pushRepository: PushRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // 初始化数据存储
        dataStore = SimpleDataStore.getInstance(this)

        // 初始化Repository（使用简化版本）
        knowledgeRepository = KnowledgeRepository(dataStore)
        progressRepository = ProgressRepository(dataStore)
        pushRepository = PushRepository(dataStore)
        settingsRepository = SettingsRepository(dataStore)
    }
}