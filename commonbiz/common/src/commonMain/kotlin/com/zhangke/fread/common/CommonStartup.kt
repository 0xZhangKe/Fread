package com.zhangke.fread.common

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.common.content.FreadContentDbMigrateManager
import me.tatarka.inject.annotations.Inject

class CommonStartup @Inject constructor(
    private val freadContentDbMigrateManager: FreadContentDbMigrateManager,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
) : ModuleStartup {

    override fun onAppCreate() {
        freadContentDbMigrateManager.migrateOldDb()
        activeAccountsSynchronizer.initialize()
    }
}