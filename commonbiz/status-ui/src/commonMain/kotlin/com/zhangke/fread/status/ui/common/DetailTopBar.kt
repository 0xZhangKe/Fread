package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.richtext.FreadRichText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    progress: Float,
    title: RichText,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    val topBarContainerColor = MaterialTheme.colorScheme.surface.copy(progress)
    val onTopBarColor = lerp(
        start = MaterialTheme.colorScheme.inverseOnSurface,
        stop = MaterialTheme.colorScheme.onSurface,
        fraction = progress,
    )
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        title = {
            if (progress >= 1F) {
                FreadRichText(
                    modifier = Modifier,
                    richText = title,
                    fontSizeSp = 22F,
                    maxLines = 1,
                    onUrlClick = {
                        coroutineScope.launch {
                            browserLauncher.launchWebTabInApp(it)
                        }
                    },
                )
            }
        },
        navigationIcon = {
            Toolbar.BackButton(onBackClick = onBackClick)
        },
        windowInsets = WindowInsets.statusBars,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topBarContainerColor,
            navigationIconContentColor = onTopBarColor,
            actionIconContentColor = onTopBarColor,
        ),
        actions = actions,
    )
}
