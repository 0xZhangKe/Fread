package com.zhangke.utopia.activitypub.app.internal.source.user

import javax.inject.Inject

class SearchUserSourceUseCase @Inject constructor(
    private val resolveUserSourceByUri: ResolveUserSourceByUriStringUseCase,
    private val resolveUserSourceByWebFinger: ResolveUserSourceByWebFingerUseCase,
) {

    suspend operator fun invoke(query: String): Result<List<UserSource>> {
        val searchResult = mutableListOf<UserSource>()
        resolveUserSourceByUri(query).getOrNull()?.let { searchResult += it }
        resolveUserSourceByWebFinger(query).getOrNull()?.let { searchResult += it }
        return Result.success(searchResult)
    }
}
