package com.zhangke.fread.common.language

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.ActivityLifecycleCallbacksAdapter
import com.zhangke.framework.utils.extractActivity
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject
import java.lang.ref.WeakReference
import java.util.Locale

@ApplicationScope
class LanguageHelper @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {

    companion object {
        private const val LANGUAGE_SETTING = "app_language_setting"

        lateinit var systemLocale: Locale
    }

    lateinit var currentType: LanguageSettingType
        private set

    private val pausedActivityList = mutableListOf<WeakReference<Activity>>()

    private lateinit var application: Application

    fun prepare(application: Application) {
        this.application = application
        systemLocale = Locale.getDefault()
        currentType = readLocalFromStorage() ?: LanguageSettingType.SYSTEM
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

    fun setLanguage(context: Context, type: LanguageSettingType) {
        this.currentType = type
        saveLocalToStorage(type)
        context.changeLanguage(type)
        context.extractActivity()?.recreate()
        if (context != application) {
            application.changeLanguage(currentType)
        }
        notifyOtherActivityConfig()
    }

    private fun Context.changeLanguage(type: LanguageSettingType) {
        val metrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.setLocale(type.toLocale())
        resources.updateConfiguration(configuration, metrics)
    }

    private fun notifyOtherActivityConfig() {
        pausedActivityList.mapNotNull { it.get() }
            .forEach { it.recreate() }
    }

    private fun saveLocalToStorage(type: LanguageSettingType) {
        ApplicationScope.launch {
            localConfigManager.putInt(LANGUAGE_SETTING, type.value)
        }
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
}

enum class LanguageSettingType(val value: Int) {

    CN(1),
    EN(2),
    SYSTEM(3),

    ;

    fun toLocale(): Locale {
        return when (this) {
            CN -> Locale.SIMPLIFIED_CHINESE
            EN -> Locale.ENGLISH
            SYSTEM -> LanguageHelper.systemLocale
        }
    }
}
