package com.zhangke.fread.activitypub.app

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.platform.PlatformSnapshot
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val platformRepo: ActivityPubPlatformRepo,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        val baseUrl = userUriTransformer.parse(sourceUri)?.baseUrl
            ?: return Result.failure(IllegalArgumentException("$sourceUri not ActivityPub uri"))
        val role = IdentityRole(accountUri = null, baseUrl = baseUrl)
        return getActivityPubServer(role)
    }

    override suspend fun getSuggestedPlatformSnapshotList(): List<PlatformSnapshot> {
        return platformRepo.getSuggestedPlatformSnapshotList()
    }

    override suspend fun resolve(blogSnapshot: PlatformSnapshot): Result<BlogPlatform>? {
        if (blogSnapshot.protocol.notActivityPub) return null
        val baseUrl = FormalBaseUrl.parse(blogSnapshot.domain) ?: return null
        return platformRepo.getPlatform(baseUrl)
    }
}
