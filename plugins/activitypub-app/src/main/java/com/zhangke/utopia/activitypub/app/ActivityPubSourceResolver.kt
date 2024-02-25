package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.usecase.source.ActivityPubSourceResolveUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class ActivityPubSourceResolver @Inject constructor(
    private val resolveActivityPubSourceByUri: ActivityPubSourceResolveUseCase,
) : IStatusSourceResolver {

    override suspend fun resolveSourceByUri(uri: FormalUri): Result<StatusSource?> {
        return resolveActivityPubSourceByUri(uri)
    }

    override suspend fun getAuthorUpdateFlow(): Flow<BlogAuthor> {
        return emptyFlow()
    }
}
