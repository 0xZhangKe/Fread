package com.zhangke.utopia.activitypub.app.internal.usecase.source.user

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class SearchUserSourceUseCase @Inject constructor(
    private val resolveUserSourceByUri: ResolveUserSourceByUriUseCase,
    private val resolveUserSourceByWebFinger: ResolveUserSourceByWebFingerUseCase,
) {

    suspend operator fun invoke(query: String): Result<List<StatusSource>> {
        val searchResult = mutableListOf<StatusSource>()
        StatusProviderUri.from(query)
            ?.let { uri -> resolveUserSourceByUri(uri).getOrNull() }
            ?.let { searchResult += it }
        resolveUserSourceByWebFinger(query).getOrNull()?.let { searchResult += it }
        return Result.success(searchResult)
    }
}
