package com.zhangke.utopia.domain

import com.zhangke.utopia.status.resolvers.StatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ResolveSourceByUriUseCase @Inject constructor(
    private val sourceResolver: StatusSourceResolver,
) {

    suspend operator fun invoke(uri: String): Result<StatusSource> {
        return sourceResolver.resolve(uri)
    }
}
