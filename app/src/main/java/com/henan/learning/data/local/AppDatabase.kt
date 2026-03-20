package com.henan.learning.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.henan.learning.data.model.KnowledgePoint
import com.henan.learning.data.model.LearningProgress
import com.henan.learning.data.model.DailyPush
import com.henan.learning.data.model.UserSettings

@Database(
    entities = [
        KnowledgePoint::class,
        LearningProgress::class,
        DailyPush::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun knowledgePointDao(): KnowledgePointDao
    abstract fun learningProgressDao(): LearningProgressDao
    abstract fun dailyPushDao(): DailyPushDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "henan_learning_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
