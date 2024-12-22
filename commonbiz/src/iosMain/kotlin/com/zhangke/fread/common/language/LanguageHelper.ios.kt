package com.zhangke.fread.common.language

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class LanguageHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {
    var currentType = readLocalFromStorage() ?: LanguageSettingType.SYSTEM
        private set

    private fun readLocalFromStorage(): LanguageSettingType? {
        return runBlocking {
            when (localConfigManager.getInt(LANGUAGE_SETTING)) {
                LanguageSettingType.CN.value -> LanguageSettingType.CN
                LanguageSettingType.EN.value -> LanguageSettingType.EN
                LanguageSettingType.SYSTEM.value -> LanguageSettingType.SYSTEM
                else -> null
            }
        }
    }

    private fun saveLocalToStorage(type: LanguageSettingType) {
        ApplicationScope.launch {
            localConfigManager.putInt(LANGUAGE_SETTING, type.value)
        }
    }

    fun setLanguage(type: LanguageSettingType) {
        currentType = type
        saveLocalToStorage(type)
    }
}

@ActivityScope
actual class ActivityLanguageHelper @Inject constructor(
    private val languageHelper: LanguageHelper,
) {
    actual val currentType get() = languageHelper.currentType

    actual fun setLanguage(type: LanguageSettingType) {
        languageHelper.setLanguage(type)
    }
}