package com.zhangke.fread.common.config

import androidx.compose.runtime.staticCompositionLocalOf
import com.russhwolf.settings.coroutines.FlowSettings
class LocalConfigManager (
    configSettingsFactory: Lazy<FlowSettings>,
) {
    private val configSettings by configSettingsFactory

    suspend fun getString(key: String): String? {
        return configSettings.getStringOrNull(key)
    }

    suspend fun getStringOrPut(key: String, block: () -> String): String {
        val value = configSettings.getStringOrNull(key)
        return value ?: block().also { configSettings.putString(key, it) }
    }

    suspend fun putString(key: String, value: String) {
        configSettings.putString(key, value)
    }

    suspend fun getInt(key: String): Int? {
        return configSettings.getIntOrNull(key)
    }

    suspend fun putInt(key: String, value: Int) {
        configSettings.putInt(key, value)
    }

    suspend fun getLong(key: String): Long?{
        return configSettings.getLongOrNull(key)
    }

    suspend fun putLong(key: String, value: Long){
        configSettings.putLong(key, value)
    }

    suspend fun getBoolean(key: String): Boolean? {
        return configSettings.getBooleanOrNull(key)
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        configSettings.putBoolean(key, value)
    }

    suspend fun removeKey(key: String) {
        configSettings.remove(key)
    }
}

val LocalLocalConfigManager =
    staticCompositionLocalOf<LocalConfigManager> { error("No LocalConfigManager provided") }