package com.henan.learning

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 开机自启动接收器
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // 开机后可以在这里启动定时推送服务
            // 暂时留空，后续添加功能
        }
    }
}