package com.zhangke.utopia.feeds.repo

import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.feeds.AbstractFeedsRepo

class MixedFeedsRepo(
    private val configId: Long,
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
) : AbstractFeedsRepo() {

    private val config = StatusConfigurationDefault.config
    private var _mixedContent: ContentConfig.MixedContent? = null

    override suspend fun getLocalStatus(): Result<List<Status>> {
        val mixedContent = getMixedContent() ?: return Result.success(emptyList())
        return feedsRepo.getLocalFirstPageStatus(
            sourceUriList = mixedContent.sourceUriList,
            limit = config.loadFromLocalLimit,
        ).let { Result.success(it) }
    }

    override suspend fun fetchRemoteStatus(maxId: String?): Result<List<Status>> {
        val mixedContent = getMixedContent() ?: return Result.failure(Exception("mixedContent is null"))
        return feedsRepo.refresh(
            sourceUriList = mixedContent!!.sourceUriList,
            limit = config.loadFromServerLimit,
        )
    }

    override suspend fun replaceLocalStatus(statuses: List<Status>) {
        TODO("Not yet implemented")
    }

    override suspend fun appendLocalStatus(statuses: List<Status>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateLocalStatus(status: Status) {
        TODO("Not yet implemented")
    }

    private suspend fun getMixedContent(): ContentConfig.MixedContent? {
        if (_mixedContent != null) return _mixedContent
        _mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
        return _mixedContent
    }
}
