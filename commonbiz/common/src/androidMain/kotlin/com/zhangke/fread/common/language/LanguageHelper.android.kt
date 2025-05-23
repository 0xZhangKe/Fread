package com.zhangke.fread.common.language

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build.VERSION
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.ActivityLifecycleCallbacksAdapter
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject
import java.lang.ref.WeakReference
import java.util.Locale

@ApplicationScope
class LanguageHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
    private val application: Application,
) {
    private val pausedActivityList = mutableListOf<WeakReference<Activity>>()

    var currentType = readLocalFromStorage() ?: LanguageSettingType.SYSTEM
        private set

    fun init() {
        application.changeLanguage(currentType)
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.changeLanguage(currentType)
                super.onActivityCreated(activity, savedInstanceState)
            }

            override fun onActivityPaused(activity: Activity) {
                val savedValue = pausedActivityList.findLast { it.get() == activity }
                if (savedValue == null) {
                    pausedActivityList += WeakReference(activity)
                }
                super.onActivityPaused(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                pausedActivityList.removeAll {
                    it.get() == activity
                }
                super.onActivityDestroyed(activity)
            }
        })
    }

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

    private fun notifyOtherActivityConfig() {
        pausedActivityList.mapNotNull { it.get() }
            .forEach { it.recreate() }
    }

    fun setLanguage(type: LanguageSettingType) {
        currentType = type
        saveLocalToStorage(type)
        application.changeLanguage(type)
        notifyOtherActivityConfig()
    }
}

@ActivityScope
actual class ActivityLanguageHelper @Inject constructor(
    private val languageHelper: LanguageHelper,
    private val activity: ComponentActivity,
) {
    actual val currentType get() = languageHelper.currentType

    actual fun setLanguage(type: LanguageSettingType) {
        languageHelper.setLanguage(type)
        activity.changeLanguage(type)
        activity.recreate()
    }
}

private fun Context.changeLanguage(type: LanguageSettingType) {
    val metrics = resources.displayMetrics
    val configuration = resources.configuration

    val targetLocale = type.toLocale() ?: Locale.getDefault()
    Locale.setDefault(targetLocale)
    if (VERSION.SDK_INT >= 24) {
        configuration.setLocales(LocaleList(targetLocale))
    } else {
        configuration.setLocale(targetLocale)
    }

    @Suppress("DEPRECATION")
    resources.updateConfiguration(configuration, metrics)
}

private fun LanguageSettingType.toLocale(): Locale? {
    return when (this) {
        LanguageSettingType.CN -> Locale.SIMPLIFIED_CHINESE
        LanguageSettingType.EN -> Locale.ENGLISH
        LanguageSettingType.SYSTEM -> null
    }
}