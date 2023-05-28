package com.zhangke.utopia.status.search

import com.zhangke.utopia.status.source.StatusProviderUri
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ResolveSourceByUriUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards IResolveSourceByUriUseCase>,
) {

    suspend operator fun invoke(uri: String): Result<StatusSource?> {
        val statusProviderUri = StatusProviderUri.create(uri) ?: return Result.success(null)
        useCases.forEach {
            val result = it(statusProviderUri)
            if (result.getOrNull() != null) return result
        }
        return Result.success(null)
    }
}

interface IResolveSourceByUriUseCase {

    suspend operator fun invoke(uri: StatusProviderUri): Result<StatusSource?>
}
