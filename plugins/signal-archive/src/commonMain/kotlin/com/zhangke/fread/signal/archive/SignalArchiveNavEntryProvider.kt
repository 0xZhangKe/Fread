package com.zhangke.fread.signal.archive

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentNavKey
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentScreen
import com.zhangke.fread.signal.archive.screens.detail.SignalArchiveDetailNavKey
import com.zhangke.fread.signal.archive.screens.detail.SignalArchiveDetailScreen
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class SignalArchiveNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<AddSignalArchiveContentNavKey> { key ->
            AddSignalArchiveContentScreen(koinViewModel { parametersOf(key.url) })
        }
        entry<SignalArchiveDetailNavKey> { key ->
            SignalArchiveDetailScreen(koinViewModel { parametersOf(key) })
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(AddSignalArchiveContentNavKey::class)
        subclass(SignalArchiveDetailNavKey::class)
    }
}
