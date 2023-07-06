package com.zhangke.utopia.status.user

import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class GetAllUserUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards IGetAllUserUseCase>
) {

    suspend operator fun invoke(): Result<List<LoggedAccount>> {
        return useCases.map { it() }.collect()
    }
}

interface IGetAllUserUseCase {

    suspend operator fun invoke(): Result<List<LoggedAccount>>
}
