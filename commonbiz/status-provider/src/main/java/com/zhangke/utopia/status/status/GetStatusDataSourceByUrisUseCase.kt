package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirstOrNull
import com.zhangke.framework.feeds.fetcher.StatusDataSource
import com.zhangke.utopia.status.utils.StatusProviderUri
import javax.inject.Inject

class GetStatusDataSourceByUrisUseCase @Inject constructor(
    private val useCaseList: List<IGetStatusDataSourceByUriUseCase>,
) {

    suspend operator fun invoke(
        uris: List<String>
    ): List<StatusDataSource<*, Status>> {
        return uris
            .map { StatusProviderUri.create(it)!! }
            .mapNotNull { uri -> useCaseList.mapFirstOrNull { it(uri) } }
    }
}

interface IGetStatusDataSourceByUriUseCase {

    suspend operator fun invoke(uri: StatusProviderUri): StatusDataSource<*, Status>?
}
