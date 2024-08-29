package com.zhangke.framework.module

import android.app.Application

interface ModuleStartup {

    suspend fun onAppCreate(application: Application)
}
