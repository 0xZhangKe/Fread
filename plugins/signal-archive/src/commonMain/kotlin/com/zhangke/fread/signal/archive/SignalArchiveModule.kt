package com.zhangke.fread.signal.archive

import com.zhangke.framework.nav.NavEntryProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val signalArchiveModule = module {

    factoryOf(::SignalArchiveNavEntryProvider) bind NavEntryProvider::class

}
