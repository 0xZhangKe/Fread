package com.zhangke.fread.status.ui.video

import androidx.compose.runtime.Composable
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.ui.image.OnBlogMediaClick

@Composable
expect fun BlogVideos(
    mediaList: List<BlogMedia>,
    hideContent: Boolean,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
)