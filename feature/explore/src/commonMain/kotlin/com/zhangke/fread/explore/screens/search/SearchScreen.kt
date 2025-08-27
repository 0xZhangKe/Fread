package com.zhangke.fread.explore.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.explore.screens.search.author.SearchedAuthorTab
import com.zhangke.fread.explore.screens.search.hashtag.SearchedHashtagTab
import com.zhangke.fread.explore.screens.search.platform.SearchedPlatformTab
import com.zhangke.fread.explore.screens.search.status.SearchedStatusTab
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.isActivityPub

class SearchScreen(
    private val locator: PlatformLocator,
    private val protocol: StatusProviderProtocol,
    private val query: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        SearchScreenContent(
            onBackClick = navigator::pop,
        )
    }

    @Composable
    private fun SearchScreenContent(
        onBackClick: () -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    onBackClick = onBackClick,
                    title = query,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { paddingValues ->
            val tabs = remember(locator, protocol, query) {
                buildList {
                    add(SearchedAuthorTab(locator, query))
                    add(SearchedStatusTab(locator, query))
                    if (protocol.isActivityPub) {
                        add(SearchedPlatformTab(locator, query))
                    }
                    add(SearchedHashtagTab(locator, query))
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState
                ) {
                    HorizontalPagerWithTab(tabList = tabs)
                }
            }
        }
    }
}
