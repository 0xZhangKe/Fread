package com.zhangke.utopia.common.status

import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class StatusIdGenerator @Inject constructor() {

    fun generate(sourceUri: FormalUri, status: Status): String {
        return "${sourceUri.host}_${sourceUri.path}_${status.id}"
    }
}
