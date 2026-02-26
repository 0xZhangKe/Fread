package com.zhangke.framework.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ToolbarTokens {

    val ContainerHeight = 64.0.dp

    val LeadingIconSize = 24.0.dp

    val TrailingIconSize = 24.0.dp

    val TopAppBarHorizontalPadding = 4.dp
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Toolbar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val navigationIcon: (@Composable (() -> Unit)) = if (onBackClick != null) {
        @Composable
        {
            Toolbar.BackButton(onBackClick = onBackClick)
        }
    } else {
        {}
    }
    TopAppBar(
        navigationIcon = navigationIcon,
        actions = actions,
        title = {
            Text(text = title)
        },
    )
}

object Toolbar {

    @Composable
    fun BackButton(
        onBackClick: () -> Unit,
        modifier: Modifier = Modifier,
        tint: Color = LocalContentColor.current,
    ) {
        IconButton(
            modifier = modifier,
            onClick = onBackClick
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                "back",
                tint = tint,
            )
        }
    }

    @Composable
    fun DownloadButton(
        modifier: Modifier = Modifier,
        tint: Color = LocalContentColor.current,
        onClick: () -> Unit,
    ) {
        SimpleIconButton(
            modifier = modifier,
            onClick = onClick,
            tint = tint,
            imageVector = Icons.Default.Download,
            contentDescription = "Download",
        )
    }

    @Composable
    fun DeleteButton(
        onDeleteClick: () -> Unit,
        modifier: Modifier = Modifier,
        tint: Color = LocalContentColor.current,
    ) {
        IconButton(
            modifier = modifier,
            onClick = onDeleteClick
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                "delete",
                tint = tint,
            )
        }
    }
}
