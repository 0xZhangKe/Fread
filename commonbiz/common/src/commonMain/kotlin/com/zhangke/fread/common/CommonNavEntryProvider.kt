package com.zhangke.fread.common

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.common.browser.UrlRedirectScreen
import com.zhangke.fread.common.browser.UrlRedirectScreenKey
import com.zhangke.fread.common.deeplink.SelectAccountForPublishScreen
import com.zhangke.fread.common.deeplink.SelectAccountForPublishScreenKey
import io.ktor.http.parametersOf
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class CommonNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SelectAccountForPublishScreenKey> {
            SelectAccountForPublishScreen(koinViewModel { parametersOf(it.text) })
        }
        entry<UrlRedirectScreenKey> {
            UrlRedirectScreen(
                uri = it.uri,
                viewModel = koinViewModel {
                    parametersOf(it.uri, it.locator, it.isFromExternal)
                },
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SelectAccountForPublishScreenKey::class)
        subclass(UrlRedirectScreenKey::class)
    }
}
