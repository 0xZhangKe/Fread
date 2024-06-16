package com.zhangke.fread.common.feeds.model

import com.zhangke.fread.status.status.model.Status

data class RefreshResult(
    val newStatus: List<Status>,
    val deletedStatus: List<Status>,
) {

    companion object {
        val EMPTY = RefreshResult(emptyList(), emptyList())
    }
}
