package com.zhangke.utopia.status.author

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
data class BlogAuthor (
    val uri: FormalUri,
    val webFinger: WebFinger,
    val name: String,
    val description: String,
    val avatar: String?,
    val emojis: List<Emoji>,
)
