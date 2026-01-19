package com.zhangke.fread.common.language

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.config.LocalConfigManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LanguageHelper (
    private val localConfigManager: LocalConfigManager,
) {

    var currentLanguage = readLocalFromStorage()
        private set

    fun initialize() {

    }

    private fun readLocalFromStorage(): LanguageSettingItem {
        return runBlocking {
            localConfigManager.getString(LOCAL_KEY_LANGUAGE)
                ?.let { LanguageSettingItem.fromLocalId(it) }
                ?: LanguageSettingItem.FollowSystem
        }
    }

    private fun saveLocalToStorage(item: LanguageSettingItem) {
        ApplicationScope.launch {
            localConfigManager.putString(LOCAL_KEY_LANGUAGE, item.localId)
        }
    }

    fun setLanguage(item: LanguageSettingItem) {
        currentLanguage = item
        saveLocalToStorage(item)
    }
}

actual class ActivityLanguageHelper(
    private val languageHelper: LanguageHelper,
) {

    actual val currentLanguage get() = languageHelper.currentLanguage

    actual fun initialize() {
        languageHelper.initialize()
    }

    actual fun setLanguage(item: LanguageSettingItem) {
        languageHelper.setLanguage(item)
    }
}