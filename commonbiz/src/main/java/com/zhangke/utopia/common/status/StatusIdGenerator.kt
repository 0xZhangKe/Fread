package com.zhangke.utopia.common.status

import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.StatusProviderUri
import javax.inject.Inject

class StatusIdGenerator @Inject constructor() {

    fun generate(sourceUri: StatusProviderUri, status: Status): String {
        return "${sourceUri}_${status.id}"
    }
}
