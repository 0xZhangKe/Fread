package com.zhangke.framework.date

import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateParser {

    fun parseAll(datetime: String): Date? {
        return parseISODate(datetime) ?: parseRfc822Date(datetime) ?: parseRfc3339Date(datetime)
        ?: parseISO8601(datetime)
    }

    fun parseISODate(datetime: String): Date? {
        return try {
            val instant = Instant.parse(datetime)
            Date.from(instant.toJavaInstant())
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
            val instant = Instant.parse(datetime)
            Date.from(instant.toJavaInstant())
        } catch (e: Throwable) {
            null
        }
    }

    fun parseISO8601(datetime: String, locale: Locale = Locale.getDefault()): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", locale).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        return try {
            format.parse(datetime)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}
