package com.zhangke.utopia.common

import android.app.Application
import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.StatusProvider
import kotlinx.coroutines.flow.drop
import javax.inject.Inject

@Filt
class CommonBizModuleStartup @Inject constructor(
    private val configRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ModuleStartup {

    override suspend fun onAppCreate(application: Application) {
        statusProvider.accountManager
            .getAllAccountFlow()
            .drop(1)
            .collect {
                configRepo.clearAllLastReadStatusId()
            }
    }
}
