package com.zhangke.fread.bluesky

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.bluesky.internal.migrate.BlueskyContentMigrator
import com.zhangke.fread.bluesky.internal.usecase.RefreshSessionUseCase
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class BskyStartup @Inject constructor(
    private val refreshSession: RefreshSessionUseCase,
    private val contentMigrator: BlueskyContentMigrator,
) : ModuleStartup {

    override fun onAppCreate() {
        ApplicationScope.launch {
            contentMigrator.migrate()
            refreshSession()
        }
    }
}
