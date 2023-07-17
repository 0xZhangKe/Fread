package com.zhangke.utopia.activitypubapp.usecase

import org.joda.time.format.ISODateTimeFormat
import java.util.Date
import javax.inject.Inject

class FormatActivityPubDatetimeToDateUseCase @Inject constructor() {

    operator fun invoke(datetime: String): Date {
        return ISODateTimeFormat.dateTime().parseDateTime(datetime).toDate()
    }
}
