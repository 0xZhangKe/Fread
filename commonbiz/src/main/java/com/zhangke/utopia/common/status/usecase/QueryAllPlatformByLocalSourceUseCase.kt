package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.repo.FeedsConfigRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class QueryAllPlatformByLocalSourceUseCase @Inject constructor(
    private val feedsConfigRepo: FeedsConfigRepo,
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(): Result<List<BlogPlatform>> {
        val allUriList = feedsConfigRepo.getAllConfig().flatMap { it.sourceUriList }
        return statusProvider.platformResolver.resolveBySourceUriList(allUriList)
    }
}
