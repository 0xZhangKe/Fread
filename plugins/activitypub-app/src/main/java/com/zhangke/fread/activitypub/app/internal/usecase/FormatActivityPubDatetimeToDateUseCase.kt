package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.framework.date.DateParser
import java.util.Date
import javax.inject.Inject

class FormatActivityPubDatetimeToDateUseCase @Inject constructor() {

    operator fun invoke(datetime: String): Date {
        return DateParser.parseISODate(datetime) ?: Date()
    }
}
