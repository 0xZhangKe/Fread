package com.zhangke.utopia.status.status

import com.zhangke.utopia.status.utils.collect
import javax.inject.Inject

class FetchStatusByUrisUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(uris: List<String>): Result<List<Status>> {
        return uris.map {
            statusProvider.requestStatuses(it)
        }.collect()
    }
}
