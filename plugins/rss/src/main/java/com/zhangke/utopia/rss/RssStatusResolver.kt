package com.zhangke.utopia.rss

import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class RssStatusResolver @Inject constructor(): IStatusResolver {

    override suspend fun getStatusList(uri: FormalUri, limit: Int, sinceId: String?, maxId: String?): Result<List<Status>>? {
        TODO("Not yet implemented")
    }

    override suspend fun checkIsFirstStatus(status: Status): Result<Boolean>? {
        TODO("Not yet implemented")
    }

    override suspend fun interactive(status: Status, interaction: StatusInteraction): Result<Status>? {
        TODO("Not yet implemented")
    }

    override suspend fun getStatusContext(status: Status): Result<StatusContext>? {
        TODO("Not yet implemented")
    }
}
