package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.account.LoggedAccount
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun ContentToolbar(
    modifier: Modifier = Modifier,
    title: String,
    account: LoggedAccount?,
    showAccountInfo: Boolean,
    showNextIcon: Boolean,
    showRefreshButton: Boolean,
    hazeState: HazeState? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onMenuClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    TopAppBar(
        modifier = modifier.then(
            if (hazeState == null) {
                Modifier
            } else {
                Modifier.hazeEffect(hazeState, HazeMaterials.ultraThick(surfaceColor))
            }
        ).pointerInput(onDoubleClick) {
            detectTapGestures(
                onDoubleTap = {
                    onDoubleClick?.invoke()
                },
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        windowInsets = windowInsets,
        navigationIcon = {
            SimpleIconButton(
                onClick = onMenuClick,
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
            )
        },
        scrollBehavior = scrollBehavior,
        title = {
            Column {
                Text(
                    modifier = Modifier.noRippleClick { onTitleClick() },
                    text = title,
                    fontSize = 18.sp,
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
