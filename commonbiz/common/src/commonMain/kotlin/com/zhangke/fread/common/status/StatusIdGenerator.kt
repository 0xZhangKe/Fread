package com.zhangke.fread.common.status

import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri

class StatusIdGenerator () {

    fun generate(sourceUri: FormalUri, status: Status): String {
        return "${sourceUri.host}_${sourceUri.path}_${status.id}"
    }
}