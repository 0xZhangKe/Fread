package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

class StatusSourceResolver(
    private val resolverList: List<IStatusSourceResolver>,
) {

    suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        resolverList.forEach {
            val result = it.resolveSourceByUri(uri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return resolverList.map { it.getAuthorUpdateFlow() }.merge()
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?>

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor>
}
