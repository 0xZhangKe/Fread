package com.zhangke.fread.common.language

import android.content.Context
import android.os.Build.VERSION
import android.os.LocaleList
import com.zhangke.fread.localization.locale
import java.util.Locale

actual class ActivityLanguageHelper(
    private val languageHelper: LanguageHelper,
) {

    actual val currentLanguage get() = languageHelper.currentLanguage

    actual fun initialize() {
        languageHelper.init()
    }

    actual fun setLanguage(item: LanguageSettingItem) {
        languageHelper.setLanguage(item)
        languageHelper.topActiveActivity?.let {
            it.changeLanguage(item)
            it.recreate()
        }
    }
}

internal fun Context.changeLanguage(item: LanguageSettingItem) {
    val metrics = resources.displayMetrics
    val configuration = resources.configuration

    val targetLocale = item.locale
    Locale.setDefault(targetLocale)
    if (VERSION.SDK_INT >= 24) {
        configuration.setLocales(LocaleList(targetLocale))
    } else {
        configuration.setLocale(targetLocale)
    }

    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, metrics)
}

private val LanguageSettingItem.locale: Locale
    get() {
        return when (this) {
            is LanguageSettingItem.FollowSystem -> Locale.getDefault()
            is LanguageSettingItem.Language -> code.locale
        }
    }
