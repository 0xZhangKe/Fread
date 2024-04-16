package com.zhangke.utopia.status.source

import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri
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

    fun resolveRoleByUri(uri: FormalUri): IdentityRole {
        return resolverList.mapFirst { it.resolveRoleByUri(uri) }
    }

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return resolverList.map { it.getAuthorUpdateFlow() }.merge()
    }
}

interface IStatusSourceResolver {

    suspend fun resolveSourceByUri(role: IdentityRole?, uri: FormalUri): Result<StatusSource?>

    fun resolveRoleByUri(uri: FormalUri): IdentityRole?

    suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor>
}
