package com.zhangke.fread.bluesky.internal.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.BlendColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    progress: Float,
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
) {
    val topBarContainerColor = MaterialTheme.colorScheme.surface.copy(progress)
    val onTopBarColor = BlendColorUtils.blend(
        fraction = progress,
        startColor = MaterialTheme.colorScheme.inverseOnSurface,
        endColor = MaterialTheme.colorScheme.onSurface,
    )
    TopAppBar(
        title = {
            if (progress >= 1F) {
                Text(
                    modifier = Modifier,
                    text = title,
                    fontSize = 22.sp,
                    maxLines = 1,
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
