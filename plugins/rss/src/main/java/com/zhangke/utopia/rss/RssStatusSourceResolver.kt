package com.zhangke.utopia.rss

import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssStatusSourceResolver @Inject constructor() : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        TODO("Not yet implemented")
    }
}
