package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.source.StatusSource
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class BlueskyStatusSourceResolver @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountAdapter: BlueskyAccountAdapter,
    private val userUriTransformer: UserUriTransformer,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        val uriInsight = userUriTransformer.parse(uri) ?: return Result.success(null)
        val locator = PlatformLocator(baseUrl = platformRepo.getAllPlatform().first().baseUrl)
        val client = clientManager.getClient(locator)
        return client.getProfileCatching(GetProfileQueryParams(Did(uriInsight.did)))
            .map { profile -> accountAdapter.createSource(profile) }
    }

    override suspend fun resolveRssSource(rssUrl: String): Result<StatusSource>? {
        return null
    }
}
