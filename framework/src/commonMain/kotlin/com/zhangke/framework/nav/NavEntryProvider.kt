package com.zhangke.framework.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder

interface NavEntryProvider {

    fun EntryProviderScope<NavKey>.build()

    fun PolymorphicModuleBuilder<NavKey>.polymorph()
}
