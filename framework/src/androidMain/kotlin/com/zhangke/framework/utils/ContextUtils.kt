@file:JvmName("ContextUtils")
package com.zhangke.framework.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import androidx.lifecycle.LifecycleOwner

@Volatile
lateinit var appContext: Context
    private set

fun initApplication(application: Application) {
    appContext = application
}

inline fun <reified T> Context.extractTarget(): T? {
    var context: Context? = this
    while (context != null) {
        if (context is T) return context
        context = if (context is ContextWrapper) context.baseContext else null
    }
    return null
}

fun Context.extractActivity(): Activity? {
    return extractTarget()
}

fun Context.extractLifecycleOwner(): LifecycleOwner? {
    return extractTarget()
}

fun Context.isDebugMode(): Boolean {
    return (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
}
