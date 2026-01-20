package com.zhangke.fread.explore

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.explore.screens.search.SearchScreen
import com.zhangke.fread.explore.screens.search.SearchScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass

class ExploreNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SearchScreenNavKey> { key ->
            SearchScreen(
                locator = key.locator,
                protocol = key.protocol,
                query = key.query,
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SearchScreenNavKey::class)
    }
}
