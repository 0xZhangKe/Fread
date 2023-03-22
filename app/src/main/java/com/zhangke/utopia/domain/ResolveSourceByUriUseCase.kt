package com.zhangke.utopia.domain

import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceResolver
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