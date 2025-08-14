package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.Serializable

@Serializable
data class ActivityPubLoggedAccount(
    val userId: String,
    val baseUrl: FormalBaseUrl,
    val url: String,
    val token: ActivityPubTokenEntity,
    val banner: String,
    val followersCount: Long,
    val followingCount: Long,
    val statusesCount: Long,
    val note: String,
    val bot: Boolean,
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
}
