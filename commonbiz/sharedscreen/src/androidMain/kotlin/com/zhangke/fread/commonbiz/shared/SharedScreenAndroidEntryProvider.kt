package com.zhangke.fread.commonbiz.shared

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.screen.FullVideoScreen
import com.zhangke.fread.commonbiz.shared.screen.FullVideoScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

class SharedScreenAndroidEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<FullVideoScreenNavKey> {
            FullVideoScreen(it.uri)
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(FullVideoScreenNavKey::class)
    }
}
