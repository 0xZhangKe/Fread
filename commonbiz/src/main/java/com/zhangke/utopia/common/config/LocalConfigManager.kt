package com.zhangke.utopia.common.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object LocalConfigManager {

    private val Context.localConfig: DataStore<Preferences> by preferencesDataStore(name = "local_config")

    suspend fun getString(context: Context, key: String): String? {
        val preferenceKey = stringPreferencesKey(key)
        return context.localConfig.data.first()[preferenceKey]
    }

    suspend fun putString(context: Context, key: String, value: String) {
        val preferenceKey = stringPreferencesKey(key)
        context.localConfig.edit {
            it[preferenceKey] = value
        }
    }
}
