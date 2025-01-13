package com.zhangke.framework.activity

import android.app.Activity
import android.app.Application
import com.zhangke.framework.utils.ActivityLifecycleCallbacksAdapter
import java.lang.ref.SoftReference

object TopActivityManager {

    private var activeActivity: SoftReference<Activity?>? = null

    val topActiveActivity: Activity? get() = activeActivity?.get()

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(
            object : ActivityLifecycleCallbacksAdapter() {
                override fun onActivityResumed(activity: Activity) {
                    super.onActivityResumed(activity)
                    activeActivity = SoftReference(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                    super.onActivityPaused(activity)
                    activeActivity?.clear()
                    activeActivity = null
                }
            },
        )
    }

    fun updateTopActivity(activity: Activity){
        activeActivity = SoftReference(activity)
    }
}
