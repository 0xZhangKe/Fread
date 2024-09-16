package com.zhangke.framework.module

interface ModuleStartup {

    suspend fun onAppCreate()
}
