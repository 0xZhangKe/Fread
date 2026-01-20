package com.zhangke.fread.startup

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.krouter.KRouter

class KRouterStartup : ModuleStartup {
    override fun onAppCreate() {
        @Suppress("UNRESOLVED_REFERENCE")
        KRouter.addRouterModule(com.zhangke.krouter.generated.AutoReducingModule())
    }
}
