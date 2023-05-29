package com.zhangke.utopia.status.source

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatusSourceCacher @Inject constructor(
    private val cachers: Set<@JvmSuppressWildcards IStatusSourceCacher>
) {

    suspend fun cache(statusSource: StatusSource) {
        cachers.forEach {
            it.cache(statusSource)
        }
    }
}

interface IStatusSourceCacher {

    suspend fun cache(statusSource: StatusSource)
}
