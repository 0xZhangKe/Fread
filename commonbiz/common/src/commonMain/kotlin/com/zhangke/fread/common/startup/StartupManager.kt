package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
class StartupManager (
    private val startupList: Set<ModuleStartup>,
) {
    fun initialize() {
        startupList.forEach {
            it.onAppCreate()
        }
    }
}