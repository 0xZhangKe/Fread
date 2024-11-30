package com.zhangke.fread.bluesky

import arrow.core.Either
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyPlatformResolver @Inject constructor() : IPlatformResolver {

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return listOf(
            PlatformSnapshot(
                domain = "bsky.social",
                description = "Bluesky is social media as it should be. Find your community among millions of users, unleash your creativity, and have some fun again.",
                thumbnail = Either.Right(Res.drawable.bluesky_logo),
                protocol = createBlueskyProtocol(),
                priority = -1,
            )
        )
    }

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        TODO("Not yet implemented")
    }
}
