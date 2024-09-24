package com.zhangke.framework.date

import com.zhangke.framework.utils.Rfc822InstantParser
import kotlinx.datetime.Instant

object DateParser {

    fun parseAll(datetime: String): Instant? {
        return parseISODate(datetime) ?: parseRfc822Date(datetime) ?: parseRfc3339Date(datetime)
        ?: parseISO8601(datetime)
    }

    fun parseISODate(datetime: String): Instant? {
        return try {
            Instant.parse(datetime)
        } catch (e: Throwable) {
            null
        }
    }

    fun parseRfc822Date(datetime: String): Instant? {
        return try {
            Rfc822InstantParser.parse(datetime)
        } catch (e: Throwable) {
            null
        }
    }

    fun parseRfc3339Date(datetime: String): Instant? {
        return try {
            Instant.parse(datetime)
        } catch (e: Throwable) {
            null
        }
    }

    fun parseISO8601(datetime: String): Instant? {
        return try {
            Instant.parse(datetime)
        } catch (e: Throwable) {
            e.printStackTrace()
            null
        }
    }
}
