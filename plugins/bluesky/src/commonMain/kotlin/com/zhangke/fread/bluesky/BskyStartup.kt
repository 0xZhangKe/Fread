package com.zhangke.fread.bluesky

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.bluesky.internal.migrate.BlueskyContentMigrator
import com.zhangke.fread.bluesky.internal.usecase.RefreshLabelersSubscriptionUseCase
import com.zhangke.fread.bluesky.internal.usecase.RefreshSessionUseCase
import kotlinx.coroutines.launch

class BskyStartup(
    private val refreshSession: RefreshSessionUseCase,
    private val contentMigrator: BlueskyContentMigrator,
    private val refreshLabelers: RefreshLabelersSubscriptionUseCase,
) : ModuleStartup {

    override fun onAppCreate() {
        ApplicationScope.launch {
            contentMigrator.migrate()
            refreshSession()
            refreshLabelers()
        }
    }
}
