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
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class HomeFeedsScreenNavKey(
    val feedsJson: String,
    val locator: PlatformLocator,
) : NavKey {

    companion object {

        fun create(feeds: BlueskyFeeds, locator: PlatformLocator): HomeFeedsScreenNavKey {
            return HomeFeedsScreenNavKey(
                locator = locator,
                feedsJson = globalJson.encodeToString(
                    serializer = BlueskyFeeds.serializer(),
                    value = feeds,
                ),
            )
        }
    }
}

@Composable
fun HomeFeedsScreen(
    feedsJson: String,
    locator: PlatformLocator,
) {
    val backStack = LocalNavBackStack.currentOrThrow
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
                title = stringResource(LocalizedString.feeds),
                onBackClick = backStack::removeLastOrNull,
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
                tab.Content()
            }
        }
    }
}
