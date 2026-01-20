package com.zhangke.fread.feeds

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreen
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreenNavKey
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeScreen
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeScreenNavKey
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreenNavKey
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsScreen
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsScreenNavKey
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreen
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class FeedsNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SelectContentTypeScreenNavKey> {
            SelectContentTypeScreen(koinViewModel())
        }
        entry<AddMixedFeedsScreenNavKey> {
            AddMixedFeedsScreen(koinViewModel { parametersOf(null) })
        }
        entry<ImportFeedsScreenNavKey> {
            ImportFeedsScreen(koinViewModel())
        }
        entry<SearchSourceForAddScreenNavKey> {
            SearchSourceForAddScreen(koinViewModel())
        }
        entry<EditMixedContentScreenNavKey> { key ->
            EditMixedContentScreen(
                koinViewModel { parametersOf(key.contentId) }
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SelectContentTypeScreenNavKey::class)
        subclass(AddMixedFeedsScreenNavKey::class)
        subclass(ImportFeedsScreenNavKey::class)
        subclass(SearchSourceForAddScreenNavKey::class)
        subclass(EditMixedContentScreenNavKey::class)
    }
}
