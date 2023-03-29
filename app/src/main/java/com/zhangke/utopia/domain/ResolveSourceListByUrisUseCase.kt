package com.zhangke.utopia.domain

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class ResolveSourceListByUrisUseCase @Inject constructor(
    private val resolveSourceByUriUseCase: ResolveSourceByUriUseCase,
) {

    suspend operator fun invoke(uris: List<String>): List<Result<StatusSource>> {
        return uris.map { resolveSourceByUriUseCase(it) }
    }
}