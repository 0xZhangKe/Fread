package com.zhangke.fread.localization

import androidx.compose.runtime.Composable

val LanguageCode.displayName: String
    @Composable
    get() = when (this) {
        LanguageCode.ZH_CN -> "简体中文"
        LanguageCode.ZH_HK -> "繁體中文（香港）"
        LanguageCode.ZH_TW -> "繁體中文（台灣）"
        LanguageCode.EN_US -> "English"
        LanguageCode.DE_DE -> "Deutsch"
        LanguageCode.ES_ES -> "Español"
        LanguageCode.FR_FR -> "Français"
        LanguageCode.JA_JP -> "日本語"
        LanguageCode.PT_PT -> "Português"
        LanguageCode.RU_RU -> "Русский"
    }
