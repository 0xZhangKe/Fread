package com.zhangke.framework.utils

import android.app.Application
import android.content.Context

@Volatile
lateinit var appContext: Context
    private set

fun initApplication(application: Application) {
    appContext = application
}
