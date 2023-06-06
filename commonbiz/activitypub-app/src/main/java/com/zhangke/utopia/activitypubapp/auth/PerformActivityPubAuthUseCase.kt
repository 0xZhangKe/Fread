package com.zhangke.utopia.activitypubapp.auth

import com.zhangke.utopia.status.auth.IPerformAuthBySourceListUseCase
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class PerformActivityPubAuthUseCase @Inject constructor(

): IPerformAuthBySourceListUseCase {

    override suspend fun invoke(sourceList: List<StatusSource>): Result<Boolean> {
        TODO("Not yet implemented")
    }
}
