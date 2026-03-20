package com.henan.learning

import android.app.Application
import com.henan.learning.data.local.SimpleDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application 类（红莲设计）
 * 
 * 职责：
 * 1. 初始化 SimpleDataStore 单例
 * 2. 启动时加载知识点 JSON
 */
class HenanLearningApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    lateinit var dataStore: SimpleDataStore
        private set

    override fun onCreate() {
        super.onCreate()

        // 初始化数据存储单例
        dataStore = SimpleDataStore.getInstance(this)

        // 后台加载知识点数据
        applicationScope.launch(Dispatchers.IO) {
            dataStore.loadKnowledgePointsFromAssets()
        }
    }
}