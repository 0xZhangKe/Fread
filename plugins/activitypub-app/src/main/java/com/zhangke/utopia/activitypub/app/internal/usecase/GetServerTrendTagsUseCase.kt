package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTag
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
