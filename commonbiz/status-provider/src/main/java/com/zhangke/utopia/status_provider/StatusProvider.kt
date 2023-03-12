package com.zhangke.utopia.status_provider

/**
 * Created by ZhangKe on 2022/12/9.
 */
object StatusProvider {

    private val providers: List<IStatusProvider> = ImplementationFinder().findImplementation()

    suspend fun requestStatuses(source: StatusSource): Result<List<Status>> {
        return providers.firstOrNull { it.applicable(source) }
            ?.requestStatuses(source)
            ?: Result.failure(
                IllegalArgumentException("${source::class.java} does not have provider!")
            )
    }
}

interface IStatusProvider {

    fun applicable(source: StatusSource): Boolean

    suspend fun requestStatuses(source: StatusSource): Result<List<Status>>
}