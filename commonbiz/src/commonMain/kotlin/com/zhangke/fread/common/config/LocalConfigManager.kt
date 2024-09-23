package com.zhangke.fread.common.config

import androidx.compose.runtime.staticCompositionLocalOf
import com.russhwolf.settings.coroutines.FlowSettings
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class LocalConfigManager @Inject constructor(
    configSettingsFactory: Lazy<FlowSettings>,
) {
    private val configSettings by configSettingsFactory

    suspend fun getString(key: String): String? {
        return configSettings.getStringOrNull(key)
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

    suspend fun getBoolean(key: String): Boolean? {
        return configSettings.getBooleanOrNull(key)
    }

    suspend fun putBoolean(key: String, value: Boolean) {
        configSettings.putBoolean(key, value)
    }
}

val LocalLocalConfigManager = staticCompositionLocalOf<LocalConfigManager> { error("No LocalConfigManager provided") }
