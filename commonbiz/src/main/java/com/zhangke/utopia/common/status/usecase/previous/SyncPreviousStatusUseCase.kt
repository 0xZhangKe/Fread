package com.zhangke.utopia.common.status.usecase.previous

import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

internal class SyncPreviousStatusUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(
        sourceUri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<Unit> {
        return Result.success(Unit)
    }
}
