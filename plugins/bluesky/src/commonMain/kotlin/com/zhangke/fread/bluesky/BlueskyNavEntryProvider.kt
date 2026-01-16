package com.zhangke.fread.bluesky

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreen
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

class BlueskyNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SearchStatusScreenNavKey> {
            SearchStatusScreen(koinViewModel())
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SearchStatusScreenNavKey::class)
    }
}
