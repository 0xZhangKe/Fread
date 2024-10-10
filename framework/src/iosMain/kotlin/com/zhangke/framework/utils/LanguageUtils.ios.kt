package com.zhangke.framework.utils

import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.availableLocaleIdentifiers
import platform.Foundation.languageIdentifier
import platform.Foundation.systemLocale

actual object LanguageUtils {
    actual fun getAllLanguages(): List<Locale> {
        return NSLocale.availableLocaleIdentifiers().map {
            NSLocale(it as String)
        }
    }
}

actual typealias Locale = NSLocale

actual val Locale.languageCode: String
    get() = objectForKey(NSLocaleLanguageCode) as? String ?: ""

actual val Locale.isO3LanguageCode: String
    get() = languageIdentifier()

actual fun Locale.getDisplayName(displayLocale: Locale): String {
    return displayNameForKey(NSLocaleLanguageCode, displayLocale).orEmpty()
}

actual fun initLocale(language: String): Locale {
    return NSLocale(language)
}

actual fun getDefaultLocale(): Locale {
    return NSLocale.systemLocale()
}
