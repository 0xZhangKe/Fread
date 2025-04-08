package com.zhangke.fread.status.author

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.prettyHandle
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
data class BlogAuthor(
    // 对于 Bluesky 来说，个人数据应该通过 DID 获取 PDS endpoint，而不是直接使用 baseUrl
    val uri: FormalUri,
    val webFinger: WebFinger,
    val handle: String,
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

    val prettyHandle: String = handle.prettyHandle()
}
