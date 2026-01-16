package com.zhangke.fread.bluesky.internal.screen.search

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.commonbiz.shared.screen.search.AbstractSearchStatusScreen
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable

@Serializable
data class SearchStatusScreenNavKey(
    val locator: PlatformLocator,
    val did: String,
) : NavKey

@Composable
fun SearchStatusScreen(viewModel: SearchStatusViewModel) {
    AbstractSearchStatusScreen(viewModel)
}
