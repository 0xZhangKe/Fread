package com.zhangke.fread.activitypub.app.internal.screen.user.status

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.PlatformLocator

class StatusListScreen(
    private val locator: PlatformLocator,
    private val type: StatusListType,
) : BaseScreen() {

    private val tab = StatusListTabStatusListScreen(
        locator = locator,
        type = type,
    )

    @Composable
    override fun Content() {
        super.Content()
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
                    tab.TabContent(this@StatusListScreen, null)
                }
            }
        }
    }
}
