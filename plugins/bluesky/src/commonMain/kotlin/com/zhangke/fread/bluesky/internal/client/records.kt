@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.bluesky.internal.client

import app.bsky.feed.Like
import app.bsky.feed.Repost
import app.bsky.graph.Block
import app.bsky.graph.Follow
import com.atproto.repo.StrongRef
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import kotlinx.datetime.Instant
import sh.christian.ozone.api.Did
import sh.christian.ozone.api.model.JsonContent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal fun likeRecord(
    subject: StrongRef,
    createdAt: Instant = Clock.System.now(),
): JsonContent {
    return Like(
        subject = subject,
        createdAt = createdAt,
    ).bskyJson()
}

internal fun repostRecord(
    subject: StrongRef,
    createdAt: Instant = Clock.System.now(),
): JsonContent {
    return Repost(
        subject = subject,
        createdAt = createdAt,
    ).bskyJson()
}

internal fun followRecord(
    did: String,
    createdAt: Instant = Clock.System.now(),
): JsonContent {
    return Follow(
        subject = Did(did),
        createdAt = createdAt,
    ).bskyJson()
}

internal fun blockRecord(
    did: String,
    createdAt: Instant = Clock.System.now(),
): JsonContent {
    return Block(
        subject = Did(did),
        createdAt = createdAt,
    ).bskyJson()
}
