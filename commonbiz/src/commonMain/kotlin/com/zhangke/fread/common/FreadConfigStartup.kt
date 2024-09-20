package com.zhangke.fread.common

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.config.FreadConfigManager
import me.tatarka.inject.annotations.Inject

class FreadConfigStartup @Inject constructor(
    private val freadConfigManager: Lazy<FreadConfigManager>,
) : ModuleStartup {
    override suspend fun onAppCreate() {
        freadConfigManager.value.initConfig()
    }
}