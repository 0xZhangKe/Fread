package com.zhangke.utopia.activitypubapp.source.user

import com.zhangke.filt.annotaions.Filt
import com.zhangke.utopia.status.source.IStatusSourceCacher
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

@Filt
class UserSourceCacher @Inject constructor(
    private val userSourceRepo: UserSourceRepo
) : IStatusSourceCacher {

    override suspend fun cache(statusSource: StatusSource) {
        if (statusSource !is UserSource) return
        userSourceRepo.save(statusSource)
    }
}