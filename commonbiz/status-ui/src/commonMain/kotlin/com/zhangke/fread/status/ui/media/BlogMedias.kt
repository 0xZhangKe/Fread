package com.zhangke.fread.status.ui.media

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.pxToDp
import com.zhangke.framework.voyager.AnimatedScreenContentScope
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.ui.image.BlogImageMedias
import com.zhangke.fread.status.ui.image.BlogMediaClickEvent
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.video.BlogVideos
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_image_sensitive_label
import org.jetbrains.compose.resources.stringResource

private var cachedContainerWidth: Dp? = null

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BlogMedias(
    modifier: Modifier,
    mediaList: List<BlogMedia>,
    indexInList: Int,
    sensitive: Boolean,
    onMediaClick: OnBlogMediaClick,
    showAlt: Boolean = true,
    blogTranslationState: BlogTranslationUiState? = null,
    animatedScreenContentScope: AnimatedScreenContentScope? = null,
) {
    val density = LocalDensity.current
    var containerWidth: Dp? by remember {
        mutableStateOf(cachedContainerWidth)
    }
    val statusConfig = LocalStatusUiConfig.current
    var hideContent by rememberSaveable(
        sensitive,
        mediaList,
        statusConfig.alwaysShowSensitiveContent,
    ) {
        mutableStateOf(sensitive && !statusConfig.alwaysShowSensitiveContent)
    }
    Box(
        modifier = modifier
            .onGloballyPositioned {
                containerWidth = it.size.width.pxToDp(density)
                cachedContainerWidth = containerWidth
            }
    ) {
        if (containerWidth != null) {
            BlogMediaContent(
                mediaList = mediaList,
                blogTranslationState = blogTranslationState,
                hideContent = hideContent,
                indexInList = indexInList,
                containerWidth = containerWidth!!,
                onMediaClick = onMediaClick,
                showAlt = showAlt,
                animatedScreenContentScope = animatedScreenContentScope,
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
                        containerColor = Color.Black.copy(alpha = 0.5F),
                        contentColor = Color.White,
                    ),
                ) {
                    Text(stringResource(Res.string.status_ui_image_sensitive_label))
                }
            } else {
                IconButton(
                    onClick = {
                        hideContent = true
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.3F),
                        contentColor = Color.White,
                    ),
                ) {
                    Icon(
                        modifier = Modifier.padding(2.dp),
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Hide Content",
                        tint = Color.White,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BlogMediaContent(
    mediaList: List<BlogMedia>,
    blogTranslationState: BlogTranslationUiState?,
    hideContent: Boolean,
    indexInList: Int,
    containerWidth: Dp,
    showAlt: Boolean,
    onMediaClick: OnBlogMediaClick,
    animatedScreenContentScope: AnimatedScreenContentScope? = null,
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
            onMediaClick = {
                onMediaClick(it.transformTranslatedEvent(blogTranslationState))
            },
            showAlt = showAlt,
            animatedScreenContentScope = animatedScreenContentScope,
        )
    }
}

private fun BlogMediaClickEvent.transformTranslatedEvent(
    blogTranslationState: BlogTranslationUiState?,
): BlogMediaClickEvent {
    val imageEvent = (this as? BlogMediaClickEvent.BlogImageClickEvent) ?: return this
    if (blogTranslationState == null) return this
    if (!blogTranslationState.showingTranslation) return this
    val attachment = blogTranslationState.blogTranslation?.attachments ?: return this
    val mediaList = imageEvent.mediaList
    if (attachment.size != mediaList.size) return this
    val newMediaList = mediaList.map { media ->
        val description =
            attachment.firstOrNull { it.id == media.id }?.description ?: media.description
        media.copy(description = description)
    }
    return imageEvent.copy(
        mediaList = newMediaList,
    )
}
