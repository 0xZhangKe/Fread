package com.zhangke.fread.signal.archive

import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri

class SignalArchiveStatusSourceResolver : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        return Result.success(null)
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
