package com.zhangke.fread.common.language

import androidx.compose.runtime.staticCompositionLocalOf

internal const val LANGUAGE_SETTING = "app_language_setting"

enum class LanguageSettingType(val value: Int) {
    CN(1),
    EN(2),
    SYSTEM(3),
    ;
}

expect class ActivityLanguageHelper {

        val currentType: LanguageSettingType

        fun setLanguage(type: LanguageSettingType)
}

val LocalActivityLanguageHelper = staticCompositionLocalOf<ActivityLanguageHelper> { error("No ActivityLanguageHelper provided") }
