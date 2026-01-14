package com.zhangke.fread.commonbiz.shared

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import kotlinx.serialization.modules.PolymorphicModuleBuilder

class SharedScreenNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
    }
}
