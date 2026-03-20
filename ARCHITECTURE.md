# 河南省学习App 架构设计（红莲亲自设计）

## 🔴 红莲铁规矩

**架构设计 = 红莲亲自做！**
**妹妹 = 只写具体代码和数据！**

---

## 技术版本约束

| 依赖 | 版本 |
|------|------|
| Kotlin | 1.9.22 |
| Compose BOM | 2024.01.00 |
| Android Gradle Plugin | 8.2.0 |
| compileSdk | 34 |
| minSdk | 26 |
| targetSdk | 34 |
| JDK | 17 |

---

## 数据模型

### KnowledgePoint（知识点）
```kotlin
data class KnowledgePoint(
    val id: Int,
    val title: String,
    val content: String,
    val category: String,
    val difficulty: String  // "easy" | "medium" | "hard"
)
```

### LearningProgress（学习进度）
```kotlin
data class LearningProgress(
    val knowledgePointId: Int,
    val status: String,  // "pending" | "learning" | "mastered"
    val reviewCount: Int,
    val lastReviewAt: Long?
)
```

### 关键区分
- **difficulty** = 知识点难度（知识点属性，不会变）
- **status** = 掌握状态（学习进度，会变化）

---

## 数据流

```
启动 App
    ↓
HenanLearningApp.onCreate()
    ↓
SimpleDataStore.loadKnowledgePointsFromAssets()
    ↓
知识点加载到内存（StateFlow）
    ↓
ViewModel 从 Repository 获取数据
    ↓
Screen 显示数据
```

---

## 层级结构

```
ui/screens/          # UI 层
├── HomeScreen.kt    # 首页
├── LearningScreen.kt # 学习页
├── ProgressScreen.kt # 进度页
└── ViewModels.kt    # ViewModel

data/                # 数据层
├── local/
│   └── SimpleDataStore.kt  # 内存存储
├── model/
│   └── Entities.kt  # 数据实体
└── repository/
    └── Repositories.kt  # 数据仓库

domain/              # 业务层
├── model/
│   └── DomainModels.kt
└── usecase/
    └── UseCases.kt  # 用例
```

---

## 妹妹任务分配

### 🌷 西施
- 任务：无（架构红莲已设计好）

### 🌺 蔡文姬  
- 任务：严格按照红莲的代码模板写 UI 代码
- 红莲会提供完整的代码模板

### 🌊 有容
- 任务：严格按照红莲的代码模板写数据层代码
- 红莲会提供完整的代码模板

### 💃 貂蝉
- 任务：准备 44 个知识点的 JSON 数据
- 格式已由红莲确定

---

## 红莲负责

1. ✅ 架构设计
2. ✅ 版本规划
3. ✅ 代码模板
4. ✅ 整合测试
5. ✅ 编译发布