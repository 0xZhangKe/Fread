package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.framework.date.DateParser
import com.zhangke.fread.common.utils.getCurrentInstant
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

class FormatActivityPubDatetimeToDateUseCase @Inject constructor() {

    operator fun invoke(datetime: String): Instant {
        return DateParser.parseAll(datetime) ?: getCurrentInstant()
    }
}
