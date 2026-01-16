package com.zhangke.fread.common.language

import androidx.compose.runtime.staticCompositionLocalOf

internal const val LOCAL_KEY_LANGUAGE = "app_language_code"

@Deprecated("Use LanguageCode directly")
enum class LanguageSettingType(val value: Int) {
    CN(1),
    EN(2),
    SYSTEM(3),
    ;
}

expect class ActivityLanguageHelper {

    val currentLanguage: LanguageSettingItem

    fun initialize()

    fun setLanguage(item: LanguageSettingItem)
}

val LocalActivityLanguageHelper =
    staticCompositionLocalOf<ActivityLanguageHelper> { error("No ActivityLanguageHelper provided") }
