package com.zhangke.fread.bluesky.internal.adapter

import app.bsky.actor.ProfileView
import app.bsky.actor.ProfileViewBasic
import app.bsky.actor.ProfileViewDetailed
import app.bsky.actor.ViewerState
import com.atproto.server.CreateSessionResponse
import com.atproto.server.RefreshSessionResponse
import com.zhangke.framework.architect.json.Empty
import com.zhangke.framework.datetime.Instant
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.prettyHandle
import com.zhangke.fread.status.model.createBlueskyProtocol
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.source.StatusSource
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
        val author =
            convertToBlogAuthor(did, createSessionResponse.handle.handle, profileViewDetailed)
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
            createAt = profileViewDetailed?.createdAt?.let { Instant(it) },
        )
    }

    fun convertToBlogAuthor(
        did: String,
        handle: String,
        profileViewDetailed: ProfileViewDetailed?,
    ): BlogAuthor {
        return BlogAuthor(
            uri = userUriTransformer.createUserUri(did),
            webFinger = WebFinger.createFromDid(did),
            handle = handle,
            name = profileViewDetailed?.displayName.orEmpty(),
            avatar = profileViewDetailed?.avatar?.uri,
            banner = profileViewDetailed?.banner?.uri,
            description = profileViewDetailed?.description.orEmpty(),
            emojis = emptyList(),
            followersCount = profileViewDetailed?.followersCount,
            followingCount = profileViewDetailed?.followsCount,
            statusesCount = profileViewDetailed?.postsCount,
            relationships = profileViewDetailed?.viewer?.let(::convertRelationship)
        )
    }

    fun convertToBlogAuthor(profile: ProfileViewBasic): BlogAuthor {
        val did = profile.did.did
        return BlogAuthor(
            uri = userUriTransformer.createUserUri(did),
            webFinger = WebFinger.createFromDid(did),
            handle = profile.handle.handle,
            name = profile.displayName.orEmpty(),
            description = "",
            avatar = profile.avatar?.uri,
            banner = null,
            emojis = emptyList(),
            followersCount = null,
            followingCount = null,
            statusesCount = null,
            relationships = profile.viewer?.let(::convertRelationship)
        )
    }

    fun convertToBlogAuthor(
        profile: ProfileView
    ): BlogAuthor {
        return BlogAuthor(
            uri = userUriTransformer.createUserUri(profile.did.did),
            webFinger = WebFinger.createFromDid(profile.did.did),
            handle = profile.handle.handle,
            name = profile.displayName.orEmpty(),
            avatar = profile.avatar?.uri,
            banner = null,
            description = profile.description.orEmpty(),
            emojis = emptyList(),
            followersCount = null,
            followingCount = null,
            statusesCount = null,
            relationships = profile.viewer?.let(::convertRelationship)
        )
    }

    fun createSource(
        profile: ProfileViewDetailed,
    ): StatusSource {
        return StatusSource(
            uri = userUriTransformer.createUserUri(profile.did.did),
            name = profile.displayName.orEmpty(),
            handle = profile.handle.handle.prettyHandle(),
            description = profile.description.orEmpty(),
            protocol = createBlueskyProtocol(),
            thumbnail = profile.avatar?.uri,
        )
    }

    fun createSource(
        profile: ProfileView,
    ): StatusSource {
        return StatusSource(
            uri = userUriTransformer.createUserUri(profile.did.did),
            name = profile.displayName.orEmpty(),
            handle = profile.handle.handle.prettyHandle(),
            description = profile.description.orEmpty(),
            protocol = createBlueskyProtocol(),
            thumbnail = profile.avatar?.uri,
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
            user = convertToBlogAuthor(newDid, profile.handle.handle, profile),
            handle = profile.handle.handle,
            did = newDid,
            createAt = profile.createdAt?.let { Instant(it) },
        )
    }

    private fun JsonContent.tryToJsonObject(): JsonObject? {
        return bskyJson.encodeToJsonElement(JsonContent.serializer(), this)
            .takeIf { it is JsonObject }?.jsonObject
    }

    fun convertRelationship(viewerState: ViewerState): Relationships {
        return Relationships(
            following = viewerState.following?.atUri != null,
            followedBy = viewerState.followedBy?.atUri != null,
            blocking = viewerState.blocking?.atUri != null,
            blockedBy = viewerState.blockedBy == true,
            muting = viewerState.muted == true,
            requested = null,
            requestedBy = null,
        )
    }
}
