package com.zhangke.fread.status.ui.style

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class StatusInfoStyle(
    val avatarSize: Dp,
    val avatarToNamePadding: Dp,
    val nameToInfoLineSpacing: Dp,
    val descStyle: TextStyle,
)

object StatusInfoStyleDefaults {

    val avatarSize = 40.dp

    val avatarToNamePadding = 8.dp

    val nameToInfoLineSpacing = 2.dp

    val descStyle: TextStyle @Composable get() = MaterialTheme.typography.bodySmall
}

@Composable
fun defaultStatusInfoStyle() = StatusInfoStyle(
    avatarSize = StatusInfoStyleDefaults.avatarSize,
    avatarToNamePadding = StatusInfoStyleDefaults.avatarToNamePadding,
    nameToInfoLineSpacing = StatusInfoStyleDefaults.nameToInfoLineSpacing,
    descStyle = StatusInfoStyleDefaults.descStyle,
)
