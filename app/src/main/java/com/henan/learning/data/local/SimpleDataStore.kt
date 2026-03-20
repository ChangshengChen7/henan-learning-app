package com.henan.learning.data.local

import android.content.Context
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

/**
 * 简化的数据存储 - 红莲亲自设计
 * 
 * 关键设计：
 * 1. 知识点（KnowledgePoint）- 从 JSON 加载
 * 2. 学习进度（LearningProgress）- 用户操作后更新
 * 3. status 区分：pending（未学）、learning（学习中）、mastered（已掌握）
 */
class SimpleDataStore(private val context: Context) {
    
    // 知识点列表（从 JSON 加载）
    private val _knowledgePoints = MutableStateFlow<List<KnowledgePoint>>(emptyList())
    val knowledgePoints: StateFlow<List<KnowledgePoint>> = _knowledgePoints.asStateFlow()
    
    // 学习进度列表（用户操作后更新）
    private val _learningProgress = MutableStateFlow<List<LearningProgress>>(emptyList())
    val learningProgress: StateFlow<List<LearningProgress>> = _learningProgress.asStateFlow()
    
    /**
     * 从 assets 加载知识点 JSON
     */
    fun loadKnowledgePointsFromAssets(fileName: String = "data/henan-knowledge.json") {
        try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val points = parseKnowledgePoints(jsonString)
            _knowledgePoints.value = points
        } catch (e: Exception) {
            e.printStackTrace()
            _knowledgePoints.value = emptyList()
        }
    }
    
    /**
     * 解析 JSON 为知识点列表
     */
    private fun parseKnowledgePoints(jsonString: String): List<KnowledgePoint> {
        val points = mutableListOf<KnowledgePoint>()
        try {
            val jsonObject = JSONObject(jsonString)
            val categories = jsonObject.getJSONArray("categories")
            
            for (i in 0 until categories.length()) {
                val categoryObj = categories.getJSONObject(i)
                val categoryName = categoryObj.getString("name")
                val items = categoryObj.getJSONArray("items")
                
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
    
    // ============ 学习进度操作 ============
    
    /**
     * 获取某个知识点的学习状态
     * 返回：pending（未学）、learning（学习中）、mastered（已掌握）
     */
    fun getStatus(kpId: Int): String {
        return _learningProgress.value.find { it.knowledgePointId == kpId }?.status ?: "pending"
    }
    
    /**
     * 开始学习某个知识点
     */
    fun startLearning(kpId: Int) {
        val current = _learningProgress.value.toMutableList()
        val index = current.indexOfFirst { it.knowledgePointId == kpId }
        val progress = LearningProgress(
            knowledgePointId = kpId,
            status = "learning",
            reviewCount = 0,
            lastReviewAt = System.currentTimeMillis()
        )
        if (index >= 0) {
            current[index] = progress
        } else {
            current.add(progress)
        }
        _learningProgress.value = current
    }
    
    /**
     * 标记某个知识点为已掌握
     */
    fun markAsMastered(kpId: Int) {
        val current = _learningProgress.value.toMutableList()
        val index = current.indexOfFirst { it.knowledgePointId == kpId }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(
                status = "mastered",
                reviewCount = existing.reviewCount + 1
            )
        } else {
            current.add(LearningProgress(
                knowledgePointId = kpId,
                status = "mastered",
                reviewCount = 1,
                lastReviewAt = System.currentTimeMillis()
            ))
        }
        _learningProgress.value = current
    }
    
    /**
     * 获取统计信息
     */
    fun getStats(): Stats {
        val total = _knowledgePoints.value.size
        val progress = _learningProgress.value
        val mastered = progress.count { it.status == "mastered" }
        val learning = progress.count { it.status == "learning" }
        val pending = total - mastered - learning
        return Stats(total, mastered, learning, pending.coerceAtLeast(0))
    }
    
    data class Stats(
        val total: Int,
        val mastered: Int,
        val learning: Int,
        val pending: Int
    )
    
    companion object {
        @Volatile
        private var INSTANCE: SimpleDataStore? = null
        
        fun getInstance(context: Context): SimpleDataStore {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SimpleDataStore(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}