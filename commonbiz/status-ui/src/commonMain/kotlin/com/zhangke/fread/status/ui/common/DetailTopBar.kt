package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.BlendColorUtils
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.richtext.FreadRichText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    progress: Float,
    title: RichText,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    val topBarContainerColor = MaterialTheme.colorScheme.surface.copy(progress)
    val onTopBarColor = BlendColorUtils.blend(
        fraction = progress,
        startColor = MaterialTheme.colorScheme.inverseOnSurface,
        endColor = MaterialTheme.colorScheme.onSurface,
    )
    val browserLauncher = LocalActivityBrowserLauncher.current
    TopAppBar(
        title = {
            if (progress >= 1F) {
                FreadRichText(
                    modifier = Modifier,
                    richText = title,
                    fontSizeSp = 22F,
                    maxLines = 1,
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(it)
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
