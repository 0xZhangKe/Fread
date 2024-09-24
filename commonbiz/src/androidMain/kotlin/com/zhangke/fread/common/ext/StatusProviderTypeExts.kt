package com.zhangke.fread.common.ext

import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.status_provider_type_activity_pub
import com.zhangke.fread.commonbiz.status_provider_type_mixed
import com.zhangke.fread.status.model.ContentType
import org.jetbrains.compose.resources.StringResource

val ContentType.nameResId: StringResource
    get() = when (this) {
        ContentType.MIXED -> Res.string.status_provider_type_mixed
        ContentType.ACTIVITY_PUB -> Res.string.status_provider_type_activity_pub
    }
