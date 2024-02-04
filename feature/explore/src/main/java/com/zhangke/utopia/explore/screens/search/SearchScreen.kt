package com.zhangke.utopia.explore.screens.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.utopia.explore.screens.search.author.SearchedAuthorTab
import com.zhangke.utopia.explore.screens.search.bar.ExplorerSearchBar
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SearchScreenContent(
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        SimpleIconButton(
                            onClick = onBackClick,
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    },
                    title = {
                        ExplorerSearchBar()
                    },
                )
            },
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
                HorizontalPagerWithTab(tabList = tabs)
            }
        }
    }
}
