package com.zhangke.fread.status.account

import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.uri.FormalUri

interface LoggedAccount {
    val uri: FormalUri
    val webFinger: WebFinger
    val platform: BlogPlatform
    val id: String?
    val userName: String
    val description: String?
    val avatar: String?
    val emojis: List<Emoji>
    val prettyHandle: String

    val humanizedName: RichText
        get() = buildRichText(
            document = userName,
            emojis = emojis,
        )

    val humanizedDescription: RichText
        get() = buildRichText(
            document = description.orEmpty(),
            emojis = emojis,
        )

    val locator: PlatformLocator
        get() = PlatformLocator(baseUrl = platform.baseUrl, accountUri = uri)

}
