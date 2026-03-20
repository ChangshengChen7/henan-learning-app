package com.henan.learning.data.local

import androidx.room.*
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.DailyPush
import kotlinx.coroutines.flow.Flow

@Dao
interface KnowledgePointDao {
    @Query("SELECT * FROM knowledge_points ORDER BY id ASC")
    fun getAllKnowledgePoints(): Flow<List<KnowledgePoint>>

    @Query("SELECT * FROM knowledge_points WHERE category = :category ORDER BY id ASC")
    fun getKnowledgePointsByCategory(category: String): Flow<List<KnowledgePoint>>

    @Query("SELECT * FROM knowledge_points WHERE id = :id")
    suspend fun getKnowledgePointById(id: Int): KnowledgePoint?

    @Query("SELECT * FROM knowledge_points WHERE difficulty = :difficulty ORDER BY id ASC")
    fun getKnowledgePointsByDifficulty(difficulty: String): Flow<List<KnowledgePoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgePoint(kp: KnowledgePoint): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(kps: List<KnowledgePoint>)

    @Update
    suspend fun updateKnowledgePoint(kp: KnowledgePoint)

    @Delete
    suspend fun deleteKnowledgePoint(kp: KnowledgePoint)

    @Query("SELECT COUNT(*) FROM knowledge_points")
    suspend fun getCount(): Int
}

@Dao
interface LearningProgressDao {
    @Query("SELECT * FROM learning_progress WHERE knowledgePointId = :kpId")
    suspend fun getProgressByKpId(kpId: Int): LearningProgress?

    @Query("SELECT * FROM learning_progress WHERE status = :status")
    fun getProgressByStatus(status: String): Flow<List<LearningProgress>>

    @Query("SELECT * FROM learning_progress ORDER BY nextReviewAt ASC")
    fun getAllProgress(): Flow<List<LearningProgress>>

    @Query("SELECT * FROM learning_progress WHERE nextReviewAt <= :time AND status = 'learning'")
    fun getDueForReview(time: Long): Flow<List<LearningProgress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: LearningProgress): Long

    @Update
    suspend fun updateProgress(progress: LearningProgress)

    @Query("UPDATE learning_progress SET status = :status WHERE knowledgePointId = :kpId")
    suspend fun updateStatus(kpId: Int, status: String)

    @Query("SELECT COUNT(*) FROM learning_progress WHERE status = :status")
    suspend fun getCountByStatus(status: String): Int
}

@Dao
interface DailyPushDao {
    @Query("SELECT * FROM daily_push WHERE isPushed = 0 ORDER BY pushTime ASC")
    fun getPendingPushes(): Flow<List<DailyPush>>

    @Query("SELECT * FROM daily_push WHERE pushTime BETWEEN :start AND :end")
    suspend fun getPushesForDay(start: Long, end: Long): List<DailyPush>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPush(push: DailyPush): Long

    @Update
    suspend fun updatePush(push: DailyPush)

    @Query("UPDATE daily_push SET isPushed = 1 WHERE id = :pushId")
    suspend fun markAsPushed(pushId: Int)
}

@Dao
interface UserSettingsDao {
    @Query("SELECT * FROM user_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): UserSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setSetting(setting: UserSettings)

    @Query("DELETE FROM user_settings WHERE `key` = :key")
    suspend fun deleteSetting(key: String)
}
