package com.zhangke.fread.rss

import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import me.tatarka.inject.annotations.Inject

class RssPlatformResolver @Inject constructor() : IPlatformResolver {

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return emptyList()
    }

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        return null
    }
}
