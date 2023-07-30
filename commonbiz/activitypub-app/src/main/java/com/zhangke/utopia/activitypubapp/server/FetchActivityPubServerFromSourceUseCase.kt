package com.zhangke.utopia.activitypubapp.server

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.activitypubapp.uri.timeline.ParseUriToTimelineUriUseCase
import com.zhangke.utopia.activitypubapp.uri.user.ParseUriToUserUriUseCase
import com.zhangke.utopia.status.server.IFetchServerFromSourceUseCase
import com.zhangke.utopia.status.server.StatusProviderServer
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

@Filt
class FetchActivityPubServerFromSourceUseCase @Inject constructor(
    private val getActivityPubServer: GetActivityPubServerUseCase,
    private val parseUriToUserUriUseCase: ParseUriToUserUriUseCase,
    private val parseTimelineUri: ParseUriToTimelineUriUseCase,
) : IFetchServerFromSourceUseCase {

    override suspend fun invoke(sourceUri: String): Result<StatusProviderServer?> {
        val uri = StatusProviderUri.create(sourceUri) ?: return Result.failure(
            IllegalArgumentException("$sourceUri not a status provider uri")
        )
        val host = parseUriToUserUriUseCase(uri)?.finger?.host
            ?: parseTimelineUri(uri)?.timelineServerHost ?: return Result.failure(
                IllegalArgumentException("$sourceUri not ActivityPub uri")
            )
        return getActivityPubServer(host)
    }
}
