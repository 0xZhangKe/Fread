package com.zhangke.fread.bluesky.internal.account

import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class BlueskyLoggedAccount(
    val user: BlogAuthor,
    val fromPlatform: BlogPlatform,
    val did: String,
    val didDoc: JsonObject,
    val handle: String,
    val email: String?,
    val emailConfirmed: Boolean?,
    val emailAuthFactor: Boolean?,
    val accessJwt: String,
    val refreshJwt: String,
    val active: Boolean?,
    val createAt: Instant? = null,
) : LoggedAccount {

    override val uri = user.uri

    override val webFinger = user.webFinger

    override val platform = fromPlatform

    override val id: String? = did

    override val userName = user.name

    override val description = user.description

    override val avatar = user.avatar

    override val emojis = user.emojis

    override val prettyHandle: String = user.prettyHandle
}
