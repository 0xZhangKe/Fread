package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.framework.feeds.fetcher.LoadParams
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.framework.feeds.fetcher.StatusSourceData
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.status.model.Status

class UserStatusDataSource(
    private val host: String,
    private val userId: String,
    private val clientManager: ActivityPubClientManager,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : StatusDataSource<String, Status> {

    override suspend fun load(
        params: LoadParams<String>
    ): Result<StatusSourceData<String, Status>> {
        if (params.pageKey == null) return Result.success(
            StatusSourceData(data = emptyList(), null)
        )
        return clientManager.getClient(host.toBaseUrl())
            .accountRepo
            .getStatuses(
                userId,
                limit = params.loadSize,
                minId = params.pageKey,
            )
            .map { list ->
                list.map { activityPubStatusAdapter.adapt(it) }
            }.map {
                StatusSourceData(
                    data = it,
                    nextPageKey = it.lastOrNull()?.let(::getDataId),
                )
            }
    }

    override fun getRefreshKey(): String = ""

    override fun getDataId(data: Status): String = data.id

    override fun getAuthId(data: Status): String = data.authId

    override fun getDatetime(data: Status): Long = data.datetime
}
