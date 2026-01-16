package com.zhangke.fread.activitypub.app.internal.screen.user.status

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable

@Serializable
data class StatusListScreenKey(
    val locator: PlatformLocator,
    val type: StatusListType,
) : NavKey

@Composable
fun StatusListScreen(locator: PlatformLocator, type: StatusListType) {
    val tab = remember(locator, type) {
        StatusListTabStatusListScreen(
            locator = locator,
            type = type,
            contentCanScrollBackward = null,
        )
    }
    val snackBarHostState = rememberSnackbarHostState()
    val navigator = LocalNavigator.currentOrThrow
    Scaffold(
        topBar = {
            Toolbar(
                title = tab.options?.title.orEmpty(),
                onBackClick = navigator::pop,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CompositionLocalProvider(
                LocalSnackbarHostState provides snackBarHostState
            ) {
                tab.Content()
            }
        }
    }
}
