package com.zhangke.fread.commonbiz.shared

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreen
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreenKey
import io.ktor.http.parametersOf
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class SharedScreenNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<MultiAccountPublishingScreenKey> {
            MultiAccountPublishingScreen(
                viewModel = koinViewModel { parametersOf(it.userUrisJson) }
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(MultiAccountPublishingScreenKey::class)
    }
}
