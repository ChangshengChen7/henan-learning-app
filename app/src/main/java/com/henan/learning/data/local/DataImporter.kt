package com.henan.learning.data.local

import android.content.Context
import com.henan.learning.data.model.KnowledgePoint
import org.json.JSONObject

/**
 * 河南省知识点数据导入工具
 * 从 assets/henan-knowledge.json 导入数据到数据库
 */
class DataImporter(private val context: Context) {

    /**
     * 从JSON解析知识点列表
     */
    fun parseKnowledgePoints(jsonString: String): List<KnowledgePoint> {
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
                    val kp = KnowledgePoint(
                        id = item.getInt("id"),
                        title = item.getString("title"),
                        content = item.getString("content"),
                        category = categoryName,
                        difficulty = estimateDifficulty(categoryName, item.optJSONArray("tags")),
                        region = "河南省"
                    )
                    points.add(kp)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return points
    }

    /**
     * 根据分类和标签估算难度
     */
    private fun estimateDifficulty(category: String, tags: org.json.JSONArray?): String {
        // 历史人物、历史文化等需要记忆的标记为简单
        val easyKeywords = listOf("人物", "美食", "民俗", "古迹", "风景")
        val hardKeywords = listOf("经济", "产业", "数据", "发展", "政策")
        
        val categoryLower = category.lowercase()
        val hasEasy = easyKeywords.any { categoryLower.contains(it) }
        val hasHard = hardKeywords.any { categoryLower.contains(it) }
        
        return when {
            hasEasy -> "easy"
            hasHard -> "medium"
            else -> "medium"
        }
    }

    /**
     * 从assets加载JSON
     */
    fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
