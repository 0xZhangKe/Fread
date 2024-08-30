package com.zhangke.fread.status.author

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
data class BlogAuthor(
    val uri: FormalUri,
    val webFinger: WebFinger,
    val name: String,
    val description: String,
    val avatar: String?,
    val emojis: List<Emoji>,
) : PlatformSerializable {

    val humanizedName: RichText by lazy {
        buildRichText(
            document = name,
            mentions = emptyList(),
            emojis = emojis,
            hashTags = emptyList(),
        )
    }
}
