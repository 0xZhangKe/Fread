package com.zhangke.framework.composable

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            Text(
                text = title,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
}
