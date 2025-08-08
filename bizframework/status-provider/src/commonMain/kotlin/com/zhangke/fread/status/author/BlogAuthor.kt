package com.zhangke.fread.status.author

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.prettyHandle
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.Relationships
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
    val userId: String? = null,
    val bot: Boolean = false,
    val followersCount: Long?,
    val followingCount: Long?,
    val statusesCount: Long?,
    val relationships: Relationships? = null,
) : PlatformSerializable {

    val humanizedName: RichText by lazy {
        buildRichText(
            document = name,
            mentions = emptyList(),
            emojis = emojis,
            hashTags = emptyList(),
        )
    }

    val humanizedDescription: RichText by lazy {
        buildRichText(
            document = description,
            mentions = emptyList(),
            emojis = emojis,
            hashTags = emptyList(),
        )
    }

    val prettyHandle: String = handle.prettyHandle()
}
