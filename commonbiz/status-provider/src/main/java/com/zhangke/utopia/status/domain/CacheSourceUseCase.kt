package com.zhangke.utopia.status.domain

import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceCacher
import javax.inject.Inject

class CacheSourceUseCase @Inject constructor(
    private val cacher: StatusSourceCacher,
) {

    suspend operator fun invoke(list: List<StatusSource>) {
        list.forEach {
            cacher.cache(it)
        }
    }
}
