# 代码模板（红莲亲自设计）

## 🔴 铁规矩

**妹妹严格按照这个模板写代码！**
**绝不允许自己修改架构！**

---

## 1. SimpleDataStore.kt（有容负责）

```kotlin
package com.henan.learning.data.local

import android.content.Context
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

class SimpleDataStore(private val context: Context) {
    
    // 知识点列表
    private val _knowledgePoints = MutableStateFlow<List<KnowledgePoint>>(emptyList())
    val knowledgePoints: StateFlow<List<KnowledgePoint>> = _knowledgePoints.asStateFlow()
    
    // 学习进度列表
    private val _learningProgress = MutableStateFlow<List<LearningProgress>>(emptyList())
    val learningProgress: StateFlow<List<LearningProgress>> = _learningProgress.asStateFlow()
    
    /**
     * 从 assets 加载知识点
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
     * 解析 JSON
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
    
    /**
     * 获取知识点掌握状态
     * 返回：status (pending/learning/mastered)
     */
    fun getStatusForKnowledgePoint(kpId: Int): String {
        return _learningProgress.value.find { it.knowledgePointId == kpId }?.status ?: "pending"
    }
    
    /**
     * 开始学习
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
     * 标记已掌握
     */
    fun markAsMastered(kpId: Int) {
        val current = _learningProgress.value.toMutableList()
        val index = current.indexOfFirst { it.knowledgePointId == kpId }
        if (index >= 0) {
            val existing = current[index]
            current[index] = existing.copy(status = "mastered")
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
```

---

## 2. HomeViewModel.kt（蔡文姬负责）

```kotlin
package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.KnowledgePoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
            // 监听知识点变化
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
```

---

## 3. LearningViewModel.kt（蔡文姬负责）

```kotlin
package com.henan.learning.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.henan.learning.data.local.SimpleDataStore
import com.henan.learning.data.model.KnowledgePoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

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
        return dataStore.getStatusForKnowledgePoint(kpId)
    }
}
```

---

## 4. Compose UI API 用法（官方文档）

### ProgressIndicator
```kotlin
// Compose BOM 2024.01.00 正确用法
LinearProgressIndicator(
    progress = { 0.7f },  // lambda 形式！
    modifier = Modifier.fillMaxWidth()
)

CircularProgressIndicator(
    progress = { 0.5f },  // lambda 形式！
    modifier = Modifier.size(100.dp)
)
```

### Icons
```kotlin
// 正确用法
Icons.Default.Home
Icons.Default.PlayArrow
Icons.Default.BarChart
Icons.Default.Book

// ❌ 错误用法
Icons.Filled.Home  // 不要用 Filled
```

---

## 🔴 妹妹只需要：

1. **貂蝉**：准备 44 个知识点 JSON 数据
2. **有容**：按模板复制 SimpleDataStore.kt
3. **蔡文姬**：按模板复制 ViewModels 和 Screen

**不要自己修改架构！严格按照模板来！**