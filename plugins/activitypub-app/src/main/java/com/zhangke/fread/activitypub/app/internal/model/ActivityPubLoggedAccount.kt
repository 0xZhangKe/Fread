package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

class ActivityPubLoggedAccount(
    val userId: String,
    uri: FormalUri,
    webFinger: WebFinger,
    platform: BlogPlatform,
    val baseUrl: FormalBaseUrl,
    name: String,
    description: String?,
    avatar: String?,
    val url: String,
    val token: ActivityPubTokenEntity,
    emojis: List<Emoji>,
) : LoggedAccount(
    uri = uri,
    webFinger = webFinger,
    platform = platform,
    userName = name,
    description = description,
    avatar = avatar,
    emojis = emojis,
) {

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + webFinger.hashCode()
        result = 31 * result + platform.hashCode()
        result = 31 * result + baseUrl.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (avatar?.hashCode() ?: 0)
        result = 31 * result + url.hashCode()
        result = 31 * result + token.hashCode()
        result = 31 * result + emojis.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActivityPubLoggedAccount) return false
        if (userId != other.userId) return false
        if (uri != other.uri) return false
        if (webFinger != other.webFinger) return false
        if (platform != other.platform) return false
        if (baseUrl != other.baseUrl) return false
        if (userName != other.userName) return false
        if (description != other.description) return false
        if (avatar != other.avatar) return false
        if (url != other.url) return false
        if (token != other.token) return false
        if (emojis != other.emojis) return false
        return true
    }
}
