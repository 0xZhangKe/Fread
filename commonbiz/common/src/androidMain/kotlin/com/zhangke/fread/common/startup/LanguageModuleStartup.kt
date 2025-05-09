package com.zhangke.fread.common.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.language.LanguageHelper
import me.tatarka.inject.annotations.Inject

class LanguageModuleStartup @Inject constructor(
    private val languageHelper: LanguageHelper,
): ModuleStartup {

    override fun onAppCreate() {
        languageHelper.init()
    }
}
