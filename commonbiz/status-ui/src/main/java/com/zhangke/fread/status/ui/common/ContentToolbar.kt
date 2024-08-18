package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.noDoubleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentToolbar(
    modifier: Modifier = Modifier,
    title: String,
    showNextIcon: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onMenuClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
    onDoubleClick: (() -> Unit)? = null,
) {
    TopAppBar(
        modifier = modifier.pointerInput(onDoubleClick) {
            detectTapGestures(
                onDoubleTap = {
                    onDoubleClick?.invoke()
                },
            )
        },
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
            Text(
                modifier = Modifier.noDoubleClick {
                    onTitleClick()
                },
                text = title,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            SimpleIconButton(
                modifier = Modifier,
                onClick = {
                    onRefreshClick()
                },
                imageVector = Icons.Default.Refresh,
                contentDescription = "Next Content"
            )
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
