package com.zhangke.utopia.common.feeds.usecase

import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class QueryAllPlatformByLocalSourceUseCase @Inject constructor(
    private val feedsRepo: FeedsRepo,
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(): Result<List<BlogPlatform>> {
        val allUriList = feedsRepo.queryAll().flatMap { it.sourceUriList }
        return statusProvider.platformResolver.resolveBySourceUriList(allUriList)
    }
}
