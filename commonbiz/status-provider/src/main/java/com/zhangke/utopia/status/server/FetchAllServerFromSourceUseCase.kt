package com.zhangke.utopia.status.server

import javax.inject.Inject

class FetchAllServerFromSourceUseCase @Inject constructor(
    private val fetchServerFromSources: Set<@JvmSuppressWildcards IFetchServerFromSourceUseCase>
) {

    suspend operator fun invoke(
        sourceUriList: List<String>,
    ): Result<List<StatusProviderServer>> {
        val list = mutableListOf<StatusProviderServer>()
        sourceUriList.forEach { source ->
            fetchServerFromSources.forEach useCase@{ fetchServerFromSources ->
                val server = fetchServerFromSources(source).getOrNull()
                if (server != null) {
                    list += server
                    return@useCase
                }
            }
        }
        return Result.success(list)
    }
}

interface IFetchServerFromSourceUseCase {

    suspend operator fun invoke(sourceUri: String): Result<StatusProviderServer?>
}
