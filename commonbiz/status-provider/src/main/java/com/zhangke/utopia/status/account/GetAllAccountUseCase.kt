package com.zhangke.utopia.status.account

import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class GetAllUserUseCase @Inject constructor(
    private val useCases: Set<@JvmSuppressWildcards IGetAllAccountUseCase>
) {

    suspend operator fun invoke(): Result<List<LoggedAccount>> {
        return useCases.map { it() }.collect()
    }
}

interface IGetAllAccountUseCase {

    suspend operator fun invoke(): Result<List<LoggedAccount>>
}
