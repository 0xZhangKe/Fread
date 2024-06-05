package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.BlendColorUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTopBar(
    progress: Float,
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit,
){
    val topBarContainerColor = MaterialTheme.colorScheme.surface.copy(progress)
    val onTopBarColor = BlendColorUtil.blend(
        fraction = progress,
        startColor = MaterialTheme.colorScheme.inverseOnSurface,
        endColor = MaterialTheme.colorScheme.onSurface,
    )
    TopAppBar(
        title = {
            if (progress >= 1F) {
                Text(text = title)
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
