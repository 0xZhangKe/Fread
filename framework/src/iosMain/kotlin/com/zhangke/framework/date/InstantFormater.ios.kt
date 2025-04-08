package com.zhangke.framework.date

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

actual class InstantFormater {

    actual fun formatToMediumDate(instant: Instant): String {
        return instant.toLocalDateTime(TimeZone.currentSystemDefault())
            .format(
                LocalDateTime.Format {
                    year()
                    char('-')
                    monthNumber()
                    char('-')
                    dayOfMonth()
                    char(' ')
                    hour()
                    char(':')
                    minute()
                    char(':')
                    second()
                }
            )
    }
}
