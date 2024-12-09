package com.zhangke.fread.bluesky.internal.account

import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.serialization.json.JsonObject

data class BlueskyLoggedAccount(
    val user: BlogAuthor,
    val fromPlatform: BlogPlatform,
    val did: String,
    val didDoc: JsonObject,
    val handle: String,
    val email: String,
    val emailConfirmed: Boolean,
    val emailAuthFactor: Boolean,
    val accessJwt: String,
    val refreshJwt: String,
    val active: Boolean,
) : LoggedAccount(
    uri = user.uri,
    webFinger = user.webFinger,
    platform = fromPlatform,
    userName = user.name,
    description = user.description,
    avatar = user.avatar,
    emojis = user.emojis,
)
