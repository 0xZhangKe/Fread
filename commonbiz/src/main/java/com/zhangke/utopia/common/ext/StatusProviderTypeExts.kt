package com.zhangke.utopia.common.ext

import com.zhangke.utopia.commonbiz.R
import com.zhangke.utopia.status.model.StatusProviderType

val StatusProviderType.nameResId: Int
    get() = when (this) {
        StatusProviderType.MIXED -> R.string.status_provider_type_mixed
        StatusProviderType.ACTIVITY_PUB -> R.string.status_provider_type_activity_pub
    }
