package com.zhangke.framework.utils

import java.util.Locale

class LanguageHelper {

    fun getAllLanguages(): List<Locale> {
        return Locale.getAvailableLocales()
            .distinctBy { it.language to it.getDisplayLanguage(Locale.ENGLISH) }
    }
}
