package com.zhangke.fread.common

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import me.tatarka.inject.annotations.Inject

class CommonBizModuleStartup @Inject constructor(
    private val context: ApplicationContext,
    private val feedsRepo: FeedsRepo,
) : ModuleStartup {

    override suspend fun onAppCreate() {
        feedsRepo.onAppCreate(ApplicationScope)
        FreadConfigManager.initConfig(context)
    }
}
