package com.zhangke.fread.bluesky

import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyPlatformResolver @Inject constructor(): IPlatformResolver {

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        TODO("Not yet implemented")
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        TODO("Not yet implemented")
    }

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        TODO("Not yet implemented")
    }
}
