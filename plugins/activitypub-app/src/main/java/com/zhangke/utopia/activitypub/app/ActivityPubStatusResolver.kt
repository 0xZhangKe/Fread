package com.zhangke.utopia.activitypub.app

import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.activitypub.app.internal.status.GetUserStatusDataSourceFromUriUseCase
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatusFromUri: GetUserStatusDataSourceFromUriUseCase,
) : IStatusResolver {

    override fun getStatusDataSourceByUri(uri: StatusProviderUri): StatusDataSource<*, Status>? {
        return getUserStatusFromUri(uri)
    }
}
