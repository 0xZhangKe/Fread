package com.zhangke.utopia.activitypub.app.internal.usecase.source.timeline

import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.source.TimelineSourceTransformer
import com.zhangke.utopia.activitypub.app.internal.uri.TimelineUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.platform.GetActivityPubPlatformUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubUrl
import com.zhangke.utopia.activitypub.app.internal.utils.toBaseUrl
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class SearchTimelineSourceUseCase @Inject constructor(
    private val getPlatform: GetActivityPubPlatformUseCase,
    private val timelineSourceTransformer: TimelineSourceTransformer,
    private val platformUriTransformer: TimelineUriTransformer,
    private val resolveTimelineSourceByUri: ResolveTimelineSourceByUriUseCase,
) {

    suspend operator fun invoke(query: String): Result<List<StatusSource>> {
        searchAsUri(query).takeIf { it.isNotEmpty() }?.let { return Result.success(it) }
        return searchAsUrl(query).let { Result.success(it) }
    }

    private suspend fun searchAsUri(query: String): List<StatusSource> {
        return FormalUri.from(query)
            ?.let { resolveTimelineSourceByUri(it) }
            ?.getOrNull()
            ?.let { source -> listOf(source) }
            ?: emptyList()
    }

    private suspend fun searchAsUrl(query: String): List<StatusSource> {
        val url = ActivityPubUrl.create(query) ?: return emptyList()
        val baseUrl = url.host.toBaseUrl()
        val platform = getPlatform(baseUrl).getOrNull() ?: return emptyList()
        return listOf(
            platformUriTransformer.build(serverBaseUrl = baseUrl, TimelineSourceType.HOME),
            platformUriTransformer.build(serverBaseUrl = baseUrl, TimelineSourceType.LOCAL),
            platformUriTransformer.build(serverBaseUrl = baseUrl, TimelineSourceType.PUBLIC),
        ).map {
            timelineSourceTransformer.createByPlatform(it, platform)
        }
    }
}
