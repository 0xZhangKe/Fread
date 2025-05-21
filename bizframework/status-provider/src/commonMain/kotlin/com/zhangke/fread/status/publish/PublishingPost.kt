package com.zhangke.fread.status.publish

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.StatusVisibility

data class PublishingPost(
    val content: String,
    val sensitive: Boolean,
    val warningText: String?,
    val languageCode: String,
    val visibility: StatusVisibility,
    val interactionSetting: PostInteractionSetting,
    val medias: List<PublishingMedia>,
)

data class PublishingMedia(
    val file: ContentProviderFile,
    val alt: String,
    val isVideo: Boolean,
)
