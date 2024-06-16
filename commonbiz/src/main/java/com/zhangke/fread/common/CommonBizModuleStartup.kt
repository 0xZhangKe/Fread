package com.zhangke.fread.common

import android.app.Application
import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import javax.inject.Inject

@Filt
class CommonBizModuleStartup @Inject constructor(
    private val feedsRepo: FeedsRepo,
) : ModuleStartup {

    override suspend fun onAppCreate(application: Application) {
        feedsRepo.onAppCreate(ApplicationScope)
    }
}
