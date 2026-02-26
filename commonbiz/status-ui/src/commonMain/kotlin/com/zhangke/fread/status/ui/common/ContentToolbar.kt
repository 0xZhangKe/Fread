package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.blur.applyBlurEffect
import com.zhangke.framework.composable.ScrollTopAppBar
import com.zhangke.framework.composable.ScrollTopAppBarColors
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.account.LoggedAccount
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun ContentToolbar(
    modifier: Modifier = Modifier,
    title: String,
    account: LoggedAccount?,
    showAccountInfo: Boolean,
    showNextIcon: Boolean,
    showRefreshButton: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
) {
    val scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
    val surfaceColor = MaterialTheme.colorScheme.surface
    ScrollTopAppBar(
        modifier = modifier.applyBlurEffect(containerColor = surfaceColor)
            .pointerInput(onDoubleClick) {
                detectTapGestures(onDoubleTap = { onDoubleClick?.invoke() })
            },
        scrollBehavior = scrollBehavior,
        colors = ScrollTopAppBarColors.default(scrolledContainerColor = scrolledContainerColor),
        navigationIcon = {
            SimpleIconButton(
                onClick = onMenuClick,
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
            )
        },
        title = {
            Column {
                Text(
                    modifier = Modifier.noRippleClick { onTitleClick() },
                    text = title,
                    style = ToolbarTokens.titleTextStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (account != null && showAccountInfo) {
                    Text(
                        modifier = Modifier.padding(top = 1.dp),
                        text = account.prettyHandle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        actions = {
            if (showRefreshButton) {
                SimpleIconButton(
                    modifier = Modifier,
                    onClick = {
                        onRefreshClick()
                    },
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Next Content"
                )
            }
            if (showNextIcon) {
                SimpleIconButton(
                    modifier = Modifier,
                    onClick = {
                        onNextClick()
                    },
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Content"
                )
            }
        },
    )
}
