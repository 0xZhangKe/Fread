package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TabsTopAppBar
import com.zhangke.framework.composable.TabsTopAppBarColors
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.account.LoggedAccount

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeContentTabsTopBar(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    scrollBehavior: TopAppBarScrollBehavior,
    tabCount: Int,
    tabContent: @Composable (index: Int) -> Unit,
    onTabClick: (index: Int) -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    title: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
    TabsTopAppBar(
        modifier = modifier,
        navigationIcon = navigationIcon,
        title = title,
        actions = actions,
        selectedTabIndex = selectedTabIndex,
        scrollBehavior = scrollBehavior,
        colors = TabsTopAppBarColors.default(scrolledContainerColor = scrolledContainerColor),
        tabContent = tabContent,
        tabCount = tabCount,
        onTabClick = onTabClick,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeContentTabsTopBar(
    modifier: Modifier = Modifier,
    title: String,
    account: LoggedAccount?,
    showAccountInfo: Boolean,
    selectedTabIndex: Int,
    tabTitles: List<String>,
    scrollBehavior: TopAppBarScrollBehavior,
    showNextIcon: Boolean = false,
    showRefreshButton: Boolean = false,
    onMenuClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
    onTabClick: (index: Int) -> Unit,
) {
    HomeContentTabsTopBar(
        modifier = modifier.doubleTapToScrollTop(onDoubleClick),
        selectedTabIndex = selectedTabIndex,
        tabCount = tabTitles.size,
        scrollBehavior = scrollBehavior,
        onTabClick = onTabClick,
        navigationIcon = {
            SimpleIconButton(
                onClick = onMenuClick,
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
            )
        },
        title = {
            Column(
                modifier = Modifier
            ) {
                Text(
                    modifier = Modifier.titleClickable(onTitleClick),
                    text = title,
                    maxLines = 1,
                    style = ToolbarTokens.titleTextStyle,
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
                    onClick = onRefreshClick,
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                )
            }
            if (showNextIcon) {
                SimpleIconButton(
                    onClick = onNextClick,
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next Content",
                )
            }
        },
        tabContent = { index ->
            Text(
                text = tabTitles[index],
                maxLines = 1,
            )
        },
    )
}

@Composable
private fun Modifier.titleClickable(onClick: () -> Unit): Modifier {
    return this.noRippleClick { onClick() }
}

private fun Modifier.doubleTapToScrollTop(onDoubleClick: (() -> Unit)?): Modifier = composed {
    if (onDoubleClick == null) {
        this
    } else {
        this.pointerInput(onDoubleClick) {
            detectTapGestures(onDoubleTap = { onDoubleClick() })
        }
    }
}
