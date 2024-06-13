package com.zhangke.utopia.status.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.SimpleIconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentToolbar(
    modifier: Modifier = Modifier,
    title: String,
    showNextIcon: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    onMenuClick: () -> Unit,
    onNextClick: () -> Unit,
    onTitleClick: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
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
                modifier = Modifier.clickable {
                    onTitleClick()
                },
                text = title,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            if (showNextIcon) {
                SimpleIconButton(
                    onClick = {
                        onNextClick()
                    },
                    imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                    contentDescription = "Next Content"
                )
            }
        },
    )
}
