package com.zhangke.framework.date

import kotlinx.datetime.Instant
import java.text.DateFormat
import java.util.Date
import java.util.Locale

actual class InstantFormater {

    actual fun formatToMediumDate(instant: Instant): String {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        val timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())
        val date = Date(instant.toEpochMilliseconds())
        return buildString {
            append(dateFormat.format(date))
            append(" ")
            append(timeFormat.format(date))
        }
    }
}
