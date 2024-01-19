package com.zhangke.utopia.activitypub.app.internal.usecase.source

import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubSourceResolveUseCase @Inject constructor(
    private val userRepo: UserRepo,
    private val baseUrlManager: BaseUrlManager,
    private val userUriTransformer: UserUriTransformer,
) {

    suspend operator fun invoke(uri: FormalUri): Result<StatusSource?> {
        return resolveUserSource(uri)
    }

    private suspend fun resolveUserSource(uri: FormalUri): Result<StatusSource?> {
        val userUriInsights = userUriTransformer.parse(uri) ?: return Result.success(null)
        return userRepo.getUserSource(
            baseUrl = baseUrlManager.decideBaseUrl(userUriInsights.baseUrl),
            userUriInsights = userUriInsights,
        )
    }
}
