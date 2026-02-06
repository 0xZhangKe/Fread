package com.zhangke.fread

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.demo.AppBarScreen
import com.zhangke.fread.demo.AppBarScreenNavKey
import com.zhangke.fread.screen.FreadHomeScreenContent
import com.zhangke.fread.screen.FreadHomeScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

class CommonNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<FreadHomeScreenNavKey> {
            FreadHomeScreenContent(koinViewModel())
        }
        entry<AppBarScreenNavKey> {
            AppBarScreen()
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(FreadHomeScreenNavKey::class)
        subclass(AppBarScreenNavKey::class)
    }
}
