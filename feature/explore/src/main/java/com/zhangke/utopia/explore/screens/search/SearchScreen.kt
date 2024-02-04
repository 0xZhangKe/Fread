package com.zhangke.utopia.explore.screens.search

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.utopia.explore.screens.search.author.SearchedAuthorTab
import com.zhangke.utopia.explore.screens.search.hashtag.SearchedHashtagTab
import com.zhangke.utopia.explore.screens.search.platform.SearchedPlatformTab
import com.zhangke.utopia.explore.screens.search.status.SearchedStatusTab

class SearchScreen(private val query: String) : Screen {

    @Composable
    override fun Content() {
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
            }
        ) { paddingValues ->
            val tabs = remember {
                listOf(
                    SearchedAuthorTab(query),
                    SearchedStatusTab(query),
                    SearchedPlatformTab(query),
                    SearchedHashtagTab(query),
                )
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
