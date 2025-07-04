package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

data class ActivityPubLoggedAccount(
    val userId: String,
    val baseUrl: FormalBaseUrl,
    val url: String,
    val token: ActivityPubTokenEntity,
    override val uri: FormalUri,
    override val webFinger: WebFinger,
    override val platform: BlogPlatform,
    override val userName: String,
    override val description: String?,
    override val avatar: String?,
    override val emojis: List<Emoji>,
) : LoggedAccount {

    val locator: PlatformLocator
        get() = PlatformLocator(baseUrl = platform.baseUrl, accountUri = uri)

    override val id: String? get() = userId

    override val prettyHandle: String
        get() {
            val handle = webFinger.toString()
            return if (handle.startsWith('@')) handle else "@$handle"
        }

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
