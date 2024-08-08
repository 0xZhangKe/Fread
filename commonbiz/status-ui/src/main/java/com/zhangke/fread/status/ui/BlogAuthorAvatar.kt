package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.imageLoader
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
    var loadSuccess by remember {
        mutableStateOf(false)
    }
    AsyncImage(
        modifier = modifier
            .clip(CircleShape)
            .freadPlaceholder(!loadSuccess)
            .let {
                if (onClick == null) {
                    it
                } else {
                    it.clickable { onClick() }
                }
            },
        model = imageUrl,
        imageLoader = LocalContext.current.imageLoader,
        onState = {
            loadSuccess = it is AsyncImagePainter.State.Success
        },
        contentDescription = "Avatar",
    )
}
