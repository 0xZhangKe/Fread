package com.zhangke.fread.localization

import java.util.Locale

val LanguageCode.locale: Locale
    get() =
        when (this) {
            LanguageCode.EN_US -> Locale.ENGLISH
            LanguageCode.DE_DE -> Locale("de", "DE")
            LanguageCode.ES_ES -> Locale("es", "ES")
            LanguageCode.FR_FR -> Locale("fr", "FR")
            LanguageCode.JA_JP -> Locale("ja", "JP")
            LanguageCode.PT_PT -> Locale("pt", "PT")
            LanguageCode.RU_RU -> Locale("ru", "RU")
            LanguageCode.ZH_CN -> Locale.SIMPLIFIED_CHINESE
            LanguageCode.ZH_HK -> Locale("zh", "HK")
            LanguageCode.ZH_TW -> Locale("zh", "TW")
        }
