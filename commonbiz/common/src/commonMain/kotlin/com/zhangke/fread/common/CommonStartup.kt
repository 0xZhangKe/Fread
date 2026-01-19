package com.zhangke.fread.common

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.common.content.FreadContentDbMigrateManager
import com.zhangke.fread.common.language.ActivityLanguageHelper

class CommonStartup (
    private val freadContentDbMigrateManager: FreadContentDbMigrateManager,
    private val activeAccountsSynchronizer: ActiveAccountsSynchronizer,
    private val activityLanguageHelper: ActivityLanguageHelper,
) : ModuleStartup {

    override fun onAppCreate() {
        freadContentDbMigrateManager.migrateOldDb()
        activeAccountsSynchronizer.initialize()
        activityLanguageHelper.initialize()
    }
}