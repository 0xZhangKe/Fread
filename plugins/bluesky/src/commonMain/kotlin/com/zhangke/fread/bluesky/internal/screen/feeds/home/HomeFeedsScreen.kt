package com.zhangke.fread.bluesky.internal.screen.feeds.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.feeds
import com.zhangke.fread.status.model.PlatformLocator
import org.jetbrains.compose.resources.stringResource

class HomeFeedsScreen(
    private val feedsJson: String,
    private val locator: PlatformLocator,
) : BaseScreen() {

    companion object {

        fun create(feeds: BlueskyFeeds, locator: PlatformLocator): HomeFeedsScreen {
            return HomeFeedsScreen(
                locator = locator,
                feedsJson = globalJson.encodeToString(
                    serializer = BlueskyFeeds.serializer(),
                    value = feeds,
                ),
            )
        }
    }

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = rememberSnackbarHostState()

        val tab = remember {
            HomeFeedsTab(
                feeds = globalJson.decodeFromString(feedsJson),
                locator = locator,
            )
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.feeds),
                    onBackClick = navigator::pop,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState,
                ) {
                    tab.TabContent(this@HomeFeedsScreen, null)
                }
            }
        }
    }
}
