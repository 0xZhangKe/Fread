package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.ui.image.BlogImageMedias
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.video.BlogVideos
import com.zhangke.utopia.statusui.R

@Composable
fun BlogMedias(
    modifier: Modifier,
    mediaList: List<BlogMedia>,
    indexInList: Int,
    sensitive: Boolean,
    onMediaClick: OnBlogMediaClick,
) {
    val density = LocalDensity.current
    var containerWidth: Dp? by remember {
        mutableStateOf(null)
    }
    var hideContent by rememberSaveable {
        mutableStateOf(sensitive)
    }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                containerWidth = it.size.width.pxToDp(density)
            }
    ) {
        if (containerWidth != null) {
            BlogMediaContent(
                mediaList = mediaList,
                hideContent = hideContent,
                indexInList = indexInList,
                containerWidth = containerWidth!!,
                onMediaClick = onMediaClick,
            )
        }
        if (sensitive) {
            if (hideContent) {
                TextButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        hideContent = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.8F),
                        contentColor = Color.White,
                    ),
                ) {
                    Text(stringResource(R.string.status_ui_image_sensitive_label))
                }
            } else {
                IconButton(
                    onClick = {
                        hideContent = true
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.8F),
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Hide Content",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@Composable
private fun BlogMediaContent(
    mediaList: List<BlogMedia>,
    hideContent: Boolean,
    indexInList: Int,
    containerWidth: Dp,
    onMediaClick: OnBlogMediaClick,
) {
    if (mediaList.firstOrNull()?.type == BlogMediaType.VIDEO) {
        BlogVideos(
            mediaList = mediaList,
            indexInList = indexInList,
            hideContent = hideContent,
            onMediaClick = onMediaClick,
        )
    } else {
        val imageMediaList =
            mediaList.filter { it.type == BlogMediaType.IMAGE || it.type == BlogMediaType.GIFV }
        BlogImageMedias(
            mediaList = imageMediaList,
            hideContent = hideContent,
            containerWidth = containerWidth,
            onMediaClick = onMediaClick,
        )
    }
}
