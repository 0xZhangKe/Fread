package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.StatusProviderUri

class ActivityPubUserAuthor(
    uri: StatusProviderUri,
    webFinger: WebFinger,
    name: String,
    description: String,
    avatar: String?,
) : BlogAuthor(
    uri = uri,
    webFinger = webFinger,
    name = name,
    description = description,
    avatar = avatar,
)
