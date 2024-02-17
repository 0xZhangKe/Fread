package com.zhangke.utopia.rss

import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssPlatformResolver @Inject constructor() : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllRecordedPlatform(): List<BlogPlatform> {
        TODO("Not yet implemented")
    }
}