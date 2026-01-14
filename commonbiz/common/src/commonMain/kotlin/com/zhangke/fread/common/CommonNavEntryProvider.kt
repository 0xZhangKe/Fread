package com.zhangke.fread.common

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class CommonNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
    }
}
