package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.resolvers.SourceMaintainerResolver
import javax.inject.Inject

class ResolveSourceMaintainerUseCase @Inject constructor(
    private val resolver: SourceMaintainerResolver,
) {

    suspend operator fun invoke(query: String): Result<StatusSourceMaintainer> {
        return resolver.resolve(query)
    }
}
