package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.ProfileViewDetailed
import com.atproto.server.CreateSessionResponse
import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.architect.json.Empty
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.model.JsonContent

class BlueskyAccountAdapter @Inject constructor(
    private val userUriTransformer: UserUriTransformer,
) {

    fun createBlueskyAccount(
        profileViewDetailed: ProfileViewDetailed?,
        createSessionResponse: CreateSessionResponse,
        platform: BlogPlatform,
    ): BlueskyLoggedAccount {
        val did = createSessionResponse.did.did
        val author = convertToBlogAuthor(did, profileViewDetailed)
        return BlueskyLoggedAccount(
            user = author,
            fromPlatform = platform,
            did = did,
            didDoc = createSessionResponse.didDoc?.tryToJsonObject()
                ?: JsonObject.Empty,
            handle = createSessionResponse.handle.handle,
            email = createSessionResponse.email,
            emailConfirmed = createSessionResponse.emailConfirmed,
            emailAuthFactor = createSessionResponse.emailAuthFactor,
            accessJwt = createSessionResponse.accessJwt,
            refreshJwt = createSessionResponse.refreshJwt,
            active = createSessionResponse.active,
        )
    }

    fun convertToBlogAuthor(
        did: String,
        profileViewDetailed: ProfileViewDetailed?,
    ): BlogAuthor {
        return BlogAuthor(
            uri = userUriTransformer.createUserUri(did),
            webFinger = WebFinger.createFromDid(did),
            name = profileViewDetailed?.displayName.orEmpty(),
            avatar = profileViewDetailed?.avatar?.uri,
            description = profileViewDetailed?.description.orEmpty(),
            emojis = emptyList(),
        )
    }

    fun updateNewSession(
        account: BlueskyLoggedAccount,
        session: RefreshSessionResponse,
    ): BlueskyLoggedAccount {
        val newDid = session.did.did
        return account.copy(
            user = account.user.copy(
                uri = userUriTransformer.createUserUri(newDid),
                webFinger = WebFinger.createFromDid(newDid),
            ),
            accessJwt = session.accessJwt,
            refreshJwt = session.refreshJwt,
            handle = session.handle.handle,
            did = session.did.did,
            didDoc = session.didDoc?.tryToJsonObject() ?: JsonObject.Empty,
            active = session.active,
        )
    }

    fun updateProfile(
        account: BlueskyLoggedAccount,
        profile: ProfileViewDetailed,
    ): BlueskyLoggedAccount {
        val newDid = profile.did.did
        return account.copy(
            user = convertToBlogAuthor(newDid, profile),
            handle = profile.handle.handle,
            did = newDid,
        )
    }

    private fun JsonContent.tryToJsonObject(): JsonObject? {
        return bskyJson.encodeToJsonElement(JsonContent.serializer(), this)
            .takeIf { it is JsonObject }?.jsonObject
    }
}
