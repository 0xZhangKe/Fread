package com.zhangke.fread.status.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.contentBottomPadding

@Composable
fun PublishingFab(
    visible: Boolean,
    modifier: Modifier = Modifier,
    onPublishClick: () -> Unit,
    bottomPadding: Dp = 32.dp,
) {
    AnimatedVisibility(
        modifier = modifier
            .navigationBarsPadding()
            .contentBottomPadding()
            .padding(bottom = bottomPadding),
        visible = visible,
        enter = scaleIn() + slideInVertically(initialOffsetY = { it }),
        exit = scaleOut() + slideOutVertically(targetOffsetY = { it }),
    ) {
        FloatingActionButton(
            onClick = onPublishClick,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Post Micro Blog",
            )
        }
    }
}
