package com.zhangke.utopia.common.feeds.usecase

import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import javax.inject.Inject

class QueryAllPlatformByLocalSourceUseCase @Inject constructor(
    private val feedsRepo: FeedsRepo,
) {

//    suspend operator fun invoke(): List<Platfor>
}