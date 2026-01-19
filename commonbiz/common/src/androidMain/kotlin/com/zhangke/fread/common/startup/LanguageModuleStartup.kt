package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.language.LanguageHelper

class LanguageModuleStartup (
    private val languageHelper: LanguageHelper,
): ModuleStartup {

    override fun onAppCreate() {
        languageHelper.init()
    }
}