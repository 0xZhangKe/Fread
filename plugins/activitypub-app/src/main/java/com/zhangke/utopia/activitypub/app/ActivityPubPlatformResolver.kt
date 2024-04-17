package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubPlatformResolver @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val getActivityPubServer: GetActivityPubPlatformUseCase,
    private val userUriTransformer: UserUriTransformer,
) : IPlatformResolver {

    override suspend fun resolveBySourceUri(sourceUri: FormalUri): Result<BlogPlatform?> {
        val baseUrl = userUriTransformer.parse(sourceUri)?.baseUrl
            ?: return Result.failure(IllegalArgumentException("$sourceUri not ActivityPub uri"))
        val role = IdentityRole(accountUri = null, baseUrl = baseUrl)
        return getActivityPubServer(role)
    }

    override suspend fun getAllRecordedPlatformForLogin(): List<BlogPlatform> {
        return platformRepo.getAllLocalPlatform()
    }
}
