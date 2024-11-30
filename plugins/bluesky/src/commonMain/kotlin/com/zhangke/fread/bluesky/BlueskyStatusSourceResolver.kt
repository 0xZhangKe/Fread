package com.zhangke.fread.bluesky

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

class BlueskyStatusSourceResolver @Inject constructor(): IStatusSourceResolver {

    override suspend fun resolveSourceByUri(
        role: IdentityRole?,
        uri: FormalUri
    ): Result<StatusSource?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return flow {  }
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        TODO("Not yet implemented")
    }
}