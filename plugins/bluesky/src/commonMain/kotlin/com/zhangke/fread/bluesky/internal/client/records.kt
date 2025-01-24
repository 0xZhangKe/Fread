package com.zhangke.fread.bluesky.internal.client

import app.bsky.feed.Like
import app.bsky.feed.Repost
import com.atproto.repo.StrongRef
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import sh.christian.ozone.api.model.JsonContent

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
