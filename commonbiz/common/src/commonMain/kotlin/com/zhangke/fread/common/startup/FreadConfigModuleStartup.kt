package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

class FreadConfigModuleStartup (
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    private val freadConfigManager: Lazy<FreadConfigManager>,
) : ModuleStartup {
    override fun onAppCreate() {
        applicationCoroutineScope.launch(Dispatchers.IO) {
            freadConfigManager.value.initConfig()
        }
    }
}