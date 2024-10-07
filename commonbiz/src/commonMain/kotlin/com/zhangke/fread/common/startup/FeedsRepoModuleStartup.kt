package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class FeedsRepoModuleStartup @Inject constructor(
    private val applicationCoroutineScope: ApplicationCoroutineScope,
    private val feedsRepo: FeedsRepo,
) : ModuleStartup {

    override fun onAppCreate() {
        feedsRepo.onAppCreate(applicationCoroutineScope)
    }
}
