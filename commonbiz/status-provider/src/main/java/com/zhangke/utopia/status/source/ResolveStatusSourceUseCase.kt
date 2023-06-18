package com.zhangke.utopia.status.source

import com.zhangke.utopia.status.resolvers.StatusSourceResolver
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ResolveStatusSourceUseCase @Inject constructor(
    private val resolver: StatusSourceResolver,
) {

    suspend operator fun invoke(query: String): Result<StatusSource> {
        return resolver.resolve(query)
    }
}
