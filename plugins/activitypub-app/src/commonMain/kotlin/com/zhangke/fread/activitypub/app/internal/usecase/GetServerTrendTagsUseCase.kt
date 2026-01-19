package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator

class GetServerTrendTagsUseCase (
    private val clientManager: ActivityPubClientManager,
    private val activityPubTagAdapter: ActivityPubTagAdapter,
) {

    suspend operator fun invoke(locator: PlatformLocator): Result<List<Hashtag>> {
        return clientManager.getClient(locator)
            .instanceRepo
            .getTrendsTags(
                limit = 10,
                offset = 0,
            ).map { list ->
                list.map { activityPubTagAdapter.adapt(it) }
            }
    }
}