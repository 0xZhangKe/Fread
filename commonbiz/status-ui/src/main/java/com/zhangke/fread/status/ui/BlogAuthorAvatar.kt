package com.zhangke.fread.status.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder


@Composable
fun BlogAuthorAvatar(
    modifier: Modifier,
    reblogAvatar: String?,
    authorAvatar: String?,
    onClick: (() -> Unit)? = null,
) {
    if (reblogAvatar.isNullOrEmpty()) {
        BlogAuthorAvatar(
            modifier = modifier,
            onClick = onClick,
            imageUrl = authorAvatar,
        )
    } else {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 6.dp, bottom = 6.dp)
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier.fillMaxSize(),
                    onClick = onClick,
                    imageUrl = authorAvatar,
                )
            }
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd),
                onClick = onClick,
                imageUrl = reblogAvatar,
            )
        }
    }
}

@Composable
fun BlogAuthorAvatar(
    modifier: Modifier,
    imageUrl: String?,
    onClick: (() -> Unit)? = null,
) {
    AutoSizeBox(
        remember(imageUrl) {
            ImageRequest(imageUrl.orEmpty())
        },
    ) { action ->
        Image(
            rememberImageActionPainter(action),
            contentDescription = "Avatar",
            modifier = modifier
                .clip(CircleShape)
                .freadPlaceholder(action is ImageAction.Loading)
                .let {
                    if (onClick == null) {
                        it
                    } else {
                        it.clickable { onClick() }
                    }
                },
        )
    }
}
