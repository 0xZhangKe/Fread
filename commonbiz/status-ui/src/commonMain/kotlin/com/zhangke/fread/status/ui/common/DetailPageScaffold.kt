package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.zhangke.framework.blur.BlurController
import com.zhangke.framework.blur.LocalBlurController
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.collapsable.ScrollUpTopBarLayout
import com.zhangke.fread.status.richtext.RichText

@Composable
fun DetailPageScaffold(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    title: RichText,
    avatar: String,
    banner: String?,
    description: RichText?,
    privateNote: String?,
    loading: Boolean,
    contentCanScrollBackward: MutableState<Boolean>,
    onBannerClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    onMaybeHashtagClick: (String) -> Unit,
    onBackClick: () -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    handleLine: @Composable () -> Unit,
    followInfoLine: @Composable () -> Unit,
    topDetailContentAction: (@Composable () -> Unit)? = null,
    bottomArea: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    DetailPageScaffold(
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        title = title,
        contentCanScrollBackward = contentCanScrollBackward,
        onBackClick = onBackClick,
        topBarActions = topBarActions,
        topDetailContent = { progress ->
            DetailHeaderContent(
                progress = progress,
                loading = loading,
                banner = banner,
                avatar = avatar,
                title = title,
                description = description,
                acctLine = handleLine,
                followInfo = followInfoLine,
                onBannerClick = onBannerClick,
                onAvatarClick = onAvatarClick,
                onUrlClick = onUrlClick,
                onMaybeHashtagClick = onMaybeHashtagClick,
                privateNote = privateNote,
                action = topDetailContentAction,
                bottomArea = bottomArea,
            )
        },
        content = content,
    )
}

@Composable
fun DetailPageScaffold(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    title: RichText,
    contentCanScrollBackward: MutableState<Boolean>,
    onBackClick: () -> Unit,
    topBarActions: @Composable RowScope.() -> Unit,
    topDetailContent: @Composable BoxScope.(Float) -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.navigationBarsPadding(),
                hostState = snackbarHostState,
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPaddings ->
        val blurController = remember { BlurController.create() }
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
            LocalBlurController provides blurController,
        ) {
            ScrollUpTopBarLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings),
                topBarContent = { progress ->
                    DetailTopBar(
                        progress = progress,
                        title = title,
                        onBackClick = onBackClick,
                        actions = topBarActions,
                    )
                },
                headerContent = { progress ->
                    topDetailContent(progress)
                },
                contentCanScrollBackward = contentCanScrollBackward,
            ) {
                content()
            }
        }
    }
}
