package com.zhangke.framework.utils

actual object LanguageUtils {

    actual fun getAllLanguages(): List<Locale> {
        return Locale.getAvailableLocales()
            .distinctBy { it.language to it.getDisplayLanguage(Locale.ENGLISH) }
    }
}

actual typealias Locale = java.util.Locale

actual val Locale.languageCode: String
    get() = this.language

actual val Locale.isO3LanguageCode: String
    get() = this.isO3Language

actual fun Locale.getDisplayName(displayLocale: Locale): String {
    return this.getDisplayLanguage(displayLocale)
}

actual fun initLocale(language: String): Locale {
    return java.util.Locale(language)
}

actual fun getDefaultLocale(): Locale {
    return java.util.Locale.getDefault()
}
