package com.zhangke.framework.date

import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

actual class InstantFormater {

    actual fun formatToMediumDate(instant: Instant): String {
        return instant.format(
            DateTimeComponents.Format {
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
