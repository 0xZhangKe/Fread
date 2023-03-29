package com.zhangke.utopia.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.resolvers.StatusSourceResolver
import javax.inject.Inject

class ResolveSourceByUriUseCase @Inject constructor(
    private val sourceResolver: StatusSourceResolver,
) {

    suspend operator fun invoke(uri: String): Result<StatusSource> {
        return runCatching { sourceResolver.resolve(uri) }
            .let {
                val data = it.getOrNull()
                if (data == null) {
                    Result.failure(NullPointerException())
                } else {
                    Result.success(data)
                }
            }
    }
}