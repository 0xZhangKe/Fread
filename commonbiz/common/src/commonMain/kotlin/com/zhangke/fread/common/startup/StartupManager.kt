package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class StartupManager @Inject constructor(
    private val startupList: Set<ModuleStartup>,
) {
    fun initialize() {
        startupList.forEach {
            it.onAppCreate()
        }
    }
}
