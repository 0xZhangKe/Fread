package com.zhangke.framework.date

import org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateParser {

    fun parseISODate(datetime: String): Date? {
        return try {
            ISODateTimeFormat.dateTime().parseDateTime(datetime).toDate()
        } catch (e: Throwable) {
            null
        }
    }

    fun parseRfc822Date(datetime: String): Date? {
        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
        return try {
            format.parse(datetime)
        } catch (e: Throwable) {
            null
        }
    }

    fun parseRfc3339Date(datetime: String): Date? {
        return try {
            ISODateTimeFormat.dateTime().parseDateTime(datetime).toDate()
        } catch (e: Throwable) {
            null
        }
    }
}
