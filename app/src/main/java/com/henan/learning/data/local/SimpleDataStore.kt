package com.henan.learning.data.local

import android.content.Context
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

/**
 * 简化的数据存储
 */
class SimpleDataStore(private val context: Context) {
    
    private val _knowledgePoints = MutableStateFlow<List<KnowledgePoint>>(emptyList())
    val knowledgePoints: StateFlow<List<KnowledgePoint>> = _knowledgePoints.asStateFlow()
    
    private val _learningProgress = MutableStateFlow<List<LearningProgress>>(emptyList())
    val learningProgress: StateFlow<List<LearningProgress>> = _learningProgress.asStateFlow()
    
    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings: StateFlow<UserSettings> = _userSettings.asStateFlow()
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()
    
    /**
     * 从 assets 加载知识点
     */
    fun loadKnowledgePointsFromAssets(fileName: String = "data/henan-knowledge.json") {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val points = parseKnowledgePoints(jsonString)
            _knowledgePoints.value = points
            _isInitialized.value = true
        } catch (e: Exception) {
            e.printStackTrace()
            _knowledgePoints.value = emptyList()
            _isInitialized.value = true
        }
    }
    
    /**
     * 从 JSON 解析知识点
     */
    private fun parseKnowledgePoints(jsonString: String): List<KnowledgePoint> {
        val points = mutableListOf<KnowledgePoint>()
        try {
            val json = JSONObject(jsonString)
            val categories = json.getJSONArray("categories")
            for (i in 0 until categories.length()) {
                val category = categories.getJSONObject(i)
                val categoryName = category.getString("name")
                val items = category.getJSONArray("items")
                for (j in 0 until items.length()) {
                    val item = items.getJSONObject(j)
                    points.add(KnowledgePoint(
                        id = item.getInt("id"),
                        title = item.getString("title"),
                        content = item.getString("content"),
                        category = categoryName,
                        difficulty = item.optString("difficulty", "medium")
                    ))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return points
    }
    
    /**
     * 获取设置
     */
    fun getSettings(): UserSettings = _userSettings.value
    
    /**
     * 更新设置
     */
    fun updateSettings(settings: UserSettings) {
        _userSettings.value = settings
    }
    
    /**
     * 更新学习进度
     */
    fun updateProgress(progress: LearningProgress) {
        val currentList = _learningProgress.value.toMutableList()
        val index = currentList.indexOfFirst { it.knowledgePointId == progress.knowledgePointId }
        if (index >= 0) {
            currentList[index] = progress
        } else {
            currentList.add(progress)
        }
        _learningProgress.value = currentList
    }
    
    /**
     * 清除所有数据
     */
    fun clearAllData() {
        _learningProgress.value = emptyList()
    }
}
