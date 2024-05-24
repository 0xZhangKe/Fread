package com.zhangke.utopia.common.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

object LocalConfigManager {

    private val Context.localConfig: DataStore<Preferences> by preferencesDataStore(name = "local_config")

    suspend fun getString(context: Context, key: String): String? {
        val preferenceKey = stringPreferencesKey(key)
        return getPreferenceValue(context, preferenceKey)
    }

    suspend fun putString(context: Context, key: String, value: String) {
        val preferenceKey = stringPreferencesKey(key)
        putPreferenceValue(context, preferenceKey, value)
    }

    suspend fun getInt(context: Context, key: String): Int? {
        val preferenceKey = intPreferencesKey(key)
        return getPreferenceValue(context, preferenceKey)
    }

    suspend fun putInt(context: Context, key: String, value: Int) {
        val preferenceKey = intPreferencesKey(key)
        putPreferenceValue(context, preferenceKey, value)
    }

    private suspend fun <T> getPreferenceValue(
        context: Context,
        key: Preferences.Key<T>,
    ): T? {
        return context.localConfig.data.first()[key]
    }

    private suspend fun <T> putPreferenceValue(
        context: Context,
        key: Preferences.Key<T>,
        value: T,
    ) {
        context.localConfig.edit {
            it[key] = value
        }
    }
}
