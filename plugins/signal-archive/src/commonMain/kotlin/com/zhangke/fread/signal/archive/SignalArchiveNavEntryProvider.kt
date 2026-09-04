package com.zhangke.fread.signal.archive

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentNavKey
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentScreen
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

class SignalArchiveNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<AddSignalArchiveContentNavKey> {
            AddSignalArchiveContentScreen()
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(AddSignalArchiveContentNavKey::class)
    }
}
