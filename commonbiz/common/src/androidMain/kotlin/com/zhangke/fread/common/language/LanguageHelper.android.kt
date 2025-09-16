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
import com.zhangke.fread.localization.LanguageCode
import com.zhangke.fread.localization.locale
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject
import java.lang.ref.WeakReference
import java.util.Locale

private const val OLD_LANGUAGE_SETTING = "app_language_setting"

@ApplicationScope
class LanguageHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
    private val application: Application,
) {
    private val pausedActivityList = mutableListOf<WeakReference<Activity>>()

    var currentLanguage: LanguageSettingItem = readLocalLanguageCode()
        private set

    fun init() {
        application.changeLanguage(currentLanguage)
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.changeLanguage(currentLanguage)
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

    private fun readLocalLanguageCode(): LanguageSettingItem {
        return runBlocking {
            val id = localConfigManager.getString(LOCAL_KEY_LANGUAGE)
                ?.let { LanguageSettingItem.fromLocalId(it) }
            if (id != null) return@runBlocking id
            tryReadOldConfigAsLanguageCode() ?: LanguageSettingItem.FollowSystem
        }
    }

    private suspend fun tryReadOldConfigAsLanguageCode(): LanguageSettingItem? {
        val type = localConfigManager.getInt(OLD_LANGUAGE_SETTING)
        if (type == null) return null
        val code = when (type) {
            LanguageSettingType.CN.value -> LanguageSettingItem.Language(LanguageCode.ZH_CN)
            LanguageSettingType.EN.value -> LanguageSettingItem.Language(LanguageCode.EN_US)
            LanguageSettingType.SYSTEM.value -> LanguageSettingItem.FollowSystem
            else -> null
        }
        if (code == null) return null
        saveLocalToStorage(code)
        localConfigManager.removeKey(OLD_LANGUAGE_SETTING)
        return code
    }

    private fun saveLocalToStorage(item: LanguageSettingItem) {
        ApplicationScope.launch {
            localConfigManager.putString(LOCAL_KEY_LANGUAGE, item.localId)
        }
    }

    private fun notifyOtherActivityConfig() {
        pausedActivityList.mapNotNull { it.get() }.forEach { it.recreate() }
    }

    fun setLanguage(item: LanguageSettingItem) {
        currentLanguage = item
        saveLocalToStorage(item)
        application.changeLanguage(item)
        notifyOtherActivityConfig()
    }
}

@ActivityScope
actual class ActivityLanguageHelper @Inject constructor(
    private val languageHelper: LanguageHelper,
    private val activity: ComponentActivity,
) {

    actual val currentLanguage get() = languageHelper.currentLanguage

    actual fun setLanguage(item: LanguageSettingItem) {
        languageHelper.setLanguage(item)
        activity.changeLanguage(item)
        activity.recreate()
    }
}

private fun Context.changeLanguage(item: LanguageSettingItem) {
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
