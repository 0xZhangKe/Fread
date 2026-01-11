package com.zhangke.fread.status

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val statusProviderModel = module {

    singleOf(::StatusProvider)
}
