package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class BlueskyStatusSourceResolver @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val platformRepo: BlueskyPlatformRepo,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(
        role: IdentityRole?,
        uri: FormalUri
    ): Result<StatusSource?> {
//        val client = role?.let { clientManager.getClient(it) }?.let {
//            platformRepo.getAllPlatform().firstOrNull()?.let {
//                clientManager.getClient(IdentityRole(baseUrl = it.baseUrl, accountUri = null))
//            }
//        } ?: return Result.success(null)
//        val role = role ?: IdentityRole(uri, null)
        return Result.success(null)
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
