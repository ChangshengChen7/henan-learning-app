package com.henan.learning.data.backup

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 数据备份管理器
 * 西施实现，红莲整合
 */
class BackupManager(private val context: Context) {
    
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    
    /**
     * 导出数据备份
     */
    suspend fun exportBackup(
        dailyPushEnabled: Boolean,
        pushTime: String,
        pushCount: Int,
        progressList: List<Triple<Int, String, Int>>
    ): Uri? {
        return try {
            // 创建备份数据
            val backupJson = JSONObject().apply {
                put("version", BACKUP_VERSION)
                put("exportTime", LocalDateTime.now().format(dateTimeFormatter))
                put("settings", JSONObject().apply {
                    put("dailyPushEnabled", dailyPushEnabled)
                    put("pushTime", pushTime)
                    put("pushCount", pushCount)
                })
                put("progress", org.json.JSONArray().apply {
                    progressList.forEach { (id, status, count) ->
                        put(JSONObject().apply {
                            put("knowledgePointId", id)
                            put("status", status)
                            put("reviewCount", count)
                        })
                    }
                })
            }
            
            val jsonString = backupJson.toString(2)
            val fileName = "henan-learning-backup-${LocalDateTime.now().format(dateFormatter)}.json"
            
            saveToDownloads(fileName, jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * 保存文件到Downloads目录
     */
    private fun saveToDownloads(fileName: String, content: String): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveUsingMediaStore(fileName, content)
        } else {
            saveUsingDirectAccess(fileName, content)
        }
    }
    
    /**
     * Android 10+ 使用 MediaStore API
     */
    private fun saveUsingMediaStore(fileName: String, content: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/json")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
        
        return uri
    }
    
    /**
     * Android 9 及以下
     */
    @Suppress("DEPRECATION")
    private fun saveUsingDirectAccess(fileName: String, content: String): Uri? {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val file = java.io.File(downloadsDir, fileName)
        file.writeText(content)
        return Uri.fromFile(file)
    }
    
    /**
     * 导入备份数据
     */
    suspend fun importBackup(uri: Uri): BackupData? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            val json = JSONObject(jsonString)
            val settingsJson = json.getJSONObject("settings")
            val progressArray = json.getJSONArray("progress")
            
            val progressList = mutableListOf<BackupProgress>()
            for (i in 0 until progressArray.length()) {
                val item = progressArray.getJSONObject(i)
                progressList.add(BackupProgress(
                    knowledgePointId = item.getInt("knowledgePointId"),
                    status = item.getString("status"),
                    reviewCount = item.getInt("reviewCount")
                ))
            }
            
            BackupData(
                version = json.getString("version"),
                exportTime = json.getString("exportTime"),
                settings = BackupSettings(
                    dailyPushEnabled = settingsJson.getBoolean("dailyPushEnabled"),
                    pushTime = settingsJson.getString("pushTime"),
                    pushCount = settingsJson.getInt("pushCount")
                ),
                progress = progressList
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}