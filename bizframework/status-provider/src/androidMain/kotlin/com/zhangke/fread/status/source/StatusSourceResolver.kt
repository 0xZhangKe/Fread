package com.zhangke.fread.status.source

import com.zhangke.framework.collections.mapFirst
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class StatusSourceResolver(
    private val resolverList: List<IStatusSourceResolver>,
) {

    /**
     * @param role 传 null 表示使用 uri 对应的服务器.
     */
    suspend fun resolveSourceByUri(role: IdentityRole?, uri: FormalUri): Result<StatusSource?> {
        resolverList.forEach {
            val result = it.resolveSourceByUri(role, uri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return resolverList.map { it.getAuthorUpdateFlow() }.merge()
    }

    suspend fun resolveRssSource(rssUrl: String): Result<StatusSource> {
        return resolverList.mapFirst { it.resolveRssSource(rssUrl) }
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(role: IdentityRole?, uri: FormalUri): Result<StatusSource?>

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor>

    suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>?
}
