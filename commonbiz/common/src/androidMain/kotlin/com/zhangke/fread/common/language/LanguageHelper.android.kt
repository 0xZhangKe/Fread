package com.zhangke.fread.common.language

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.utils.ActivityLifecycleCallbacksAdapter
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.localization.LanguageCode
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

private const val OLD_LANGUAGE_SETTING = "app_language_setting" class LanguageHelper (
    private val localConfigManager: LocalConfigManager,
    private val context: Context,
) {

    private var activeActivity: WeakReference<Activity?>? = null

    val topActiveActivity: Activity? get() = activeActivity?.get()

    private val pausedActivityList = mutableListOf<WeakReference<Activity>>()

    var currentLanguage: LanguageSettingItem = readLocalLanguageCode()
        private set

    private val application = context.applicationContext as Application

    fun init() {
        application.changeLanguage(currentLanguage)
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallbacksAdapter() {

            override fun onActivityResumed(activity: Activity) {
                super.onActivityResumed(activity)
                activeActivity = WeakReference(activity)
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activity.changeLanguage(currentLanguage)
                super.onActivityCreated(activity, savedInstanceState)
            }

            override fun onActivityPaused(activity: Activity) {
                val savedValue = pausedActivityList.findLast { it.get() == activity }
                if (savedValue == null) {
                    pausedActivityList += WeakReference(activity)
                }
                activeActivity?.clear()
                activeActivity = null
                super.onActivityPaused(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                pausedActivityList.removeAll {
                    it.get() == activity
                }
                activeActivity?.clear()
                activeActivity = null
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
        val type = localConfigManager.getInt(OLD_LANGUAGE_SETTING) ?: return null
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