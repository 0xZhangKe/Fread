package com.zhangke.fread.rss

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.framework.utils.UrlEncoder
import com.zhangke.fread.rss.internal.screen.source.RssSourceScreen
import com.zhangke.fread.rss.internal.screen.source.RssSourceScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class RssNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<RssSourceScreenNavKey> { key ->
            RssSourceScreen(
                viewModel = koinViewModel {
                    parametersOf(UrlEncoder.decode(key.url))
                },
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(RssSourceScreenNavKey::class)
    }
}
