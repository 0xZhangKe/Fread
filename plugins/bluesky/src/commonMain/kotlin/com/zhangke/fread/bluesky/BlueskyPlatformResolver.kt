package com.zhangke.fread.bluesky

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import me.tatarka.inject.annotations.Inject

class BlueskyPlatformResolver @Inject constructor(
    private val blueskyPlatformRepo: BlueskyPlatformRepo,
) : IPlatformResolver {

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        if (blogSnapshot.protocol.notBluesky) return null
        val platform = blogSnapshot.toSnapshot()
            ?: return Result.failure(IllegalArgumentException("Invalid platform snapshot"))
        return Result.success(platform)
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return blueskyPlatformRepo.getAllPlatform().map { it.toSnapshot() }
    }

    private fun PlatformSnapshot.toSnapshot(): BlogPlatform? {
        return BlogPlatform(
            uri = uri ?: return null,
            name = name ?: return null,
            baseUrl = FormalBaseUrl.parse(domain)!!,
            description = description,
            thumbnail = thumbnail,
            protocol = protocol,
        )
    }

    private fun BlogPlatform.toSnapshot(): PlatformSnapshot {
        return PlatformSnapshot(
            uri = uri,
            name = name,
            domain = baseUrl.host,
            description = description,
            thumbnail = thumbnail.orEmpty(),
            protocol = protocol,
            priority = -1,
        )
    }
}
