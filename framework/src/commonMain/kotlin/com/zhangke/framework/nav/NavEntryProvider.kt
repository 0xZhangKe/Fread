package com.zhangke.framework.nav

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.PolymorphicModuleBuilder

interface NavEntryProvider {

    fun EntryProviderScope<NavKey>.build()

    fun PolymorphicModuleBuilder<NavKey>.polymorph()
}
