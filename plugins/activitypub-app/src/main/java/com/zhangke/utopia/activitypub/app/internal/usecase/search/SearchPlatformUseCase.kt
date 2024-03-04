package com.zhangke.utopia.activitypub.app.internal.usecase.search

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class SearchPlatformUseCase @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
) {

    suspend operator fun invoke(query: String): Result<List<BlogPlatform>> {
        val baseUrl = FormalBaseUrl.parse(query) ?: return Result.success(emptyList())
        return platformRepo.getPlatform(baseUrl).map { listOf(it) }
    }
}
