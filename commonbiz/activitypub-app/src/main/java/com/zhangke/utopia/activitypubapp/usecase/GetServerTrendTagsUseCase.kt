package com.zhangke.utopia.activitypubapp.usecase

import com.zhangke.activitypub.entry.ActivityPubTagEntity
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.model.ActivityPubTag
import javax.inject.Inject

class GetServerTrendTagsUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val activityPubTagAdapter: ActivityPubTagAdapter,
) {

    suspend operator fun invoke(host: String): Result<List<ActivityPubTag>> {
        return obtainActivityPubClientUseCase(host).instanceRepo
            .getTrendsTags(
                limit = 10,
                offset = 0,
            ).map { list ->
                list.map { activityPubTagAdapter.adapt(it) }
            }
    }
}
