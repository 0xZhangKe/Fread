package com.zhangke.fread.common

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.language.LanguageHelper
import me.tatarka.inject.annotations.Inject

class CommonModuleStartup @Inject constructor(
    private val languageHelper: LanguageHelper,
): ModuleStartup {

    override fun onAppCreate() {
        languageHelper.init()
    }
}
