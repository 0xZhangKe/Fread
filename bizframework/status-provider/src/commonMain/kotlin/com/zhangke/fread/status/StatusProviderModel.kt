package com.zhangke.fread.status

import org.koin.dsl.module

val statusProviderModel = module {

    single { StatusProvider(getAll<IStatusProvider>()) }
}
