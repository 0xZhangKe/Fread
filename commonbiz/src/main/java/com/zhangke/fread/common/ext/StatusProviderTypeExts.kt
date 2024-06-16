package com.zhangke.fread.common.ext

import com.zhangke.fread.commonbiz.R
import com.zhangke.fread.status.model.ContentType

val ContentType.nameResId: Int
    get() = when (this) {
        ContentType.MIXED -> R.string.status_provider_type_mixed
        ContentType.ACTIVITY_PUB -> R.string.status_provider_type_activity_pub
    }
