package com.zhangke.fread.common

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.content.FreadContentDbMigrateManager
import me.tatarka.inject.annotations.Inject

class CommonStartup @Inject constructor(
    private val freadContentDbMigrateManager: FreadContentDbMigrateManager,
) : ModuleStartup {

    override fun onAppCreate() {
        freadContentDbMigrateManager.migrateOldDb()
    }
}