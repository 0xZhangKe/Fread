package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyPlatformResolver @Inject constructor(
    private val blueskyPlatformRepo: BlueskyPlatformRepo,
) : IPlatformResolver {

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return blueskyPlatformRepo.getAllPlatform().map { it.toSnapshot() }
    }

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        TODO("Not yet implemented")
    }

    private fun BlogPlatform.toSnapshot(): PlatformSnapshot {
        return PlatformSnapshot(
            domain = uri,
            description = description,
            thumbnail = thumbnail.orEmpty(),
            protocol = protocol,
            priority = -1,
        )
    }
}
