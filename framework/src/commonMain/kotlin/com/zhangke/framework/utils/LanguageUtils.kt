package com.zhangke.framework.utils

expect object LanguageUtils {
    fun getAllLanguages(): List<Locale>
}

expect class Locale

expect val Locale.languageCode: String

expect val Locale.isO3LanguageCode: String

expect fun Locale.getDisplayName(displayLocale: Locale): String

expect fun initLocale(language: String): Locale

expect fun getDefaultLocale(): Locale
