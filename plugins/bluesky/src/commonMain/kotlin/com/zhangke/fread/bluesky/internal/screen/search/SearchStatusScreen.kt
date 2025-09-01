package com.zhangke.fread.bluesky.internal.screen.search

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.fread.commonbiz.shared.screen.search.AbstractSearchStatusScreen
import com.zhangke.fread.commonbiz.shared.screen.search.AbstractSearchStatusViewModel
import com.zhangke.fread.status.model.PlatformLocator

class SearchStatusScreen(
    private val locator: PlatformLocator,
    private val did: String,
) : AbstractSearchStatusScreen() {

    @Composable
    override fun createViewModel(): AbstractSearchStatusViewModel {
        return getViewModel<SearchStatusViewModel, SearchStatusViewModel.Factory> {
            it.create(locator, did)
        }
    }
}
