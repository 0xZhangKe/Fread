package com.zhangke.framework.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone
import kotlinx.datetime.toInstant

/**
 * RFC 822 date/time format to [Instant] parser.
 *
 * See https://www.rfc-editor.org/rfc/rfc822#section-5
 */
object Rfc822InstantParser {

    private enum class Month {
        Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec,
        ;
    }

    /**
     * Parse RFC 822 date/time format to [Instant]
     *
     * @throws IllegalArgumentException in case input string cannot be parsed as RFC 822
     */
    fun parse(input: String): Instant {
        /**
         * Groups:
         *
         * 1 = day of month (1-31)
         * 2 = month (Jan, Feb, Mar, ...)
         * 3 = year (2022)
         * 4 = hour (00-23)
         * 5 = minute (00-59)
         * 6 = OPTIONAL: second (00-59)
         * 7 = time zone (+/-hhmm or letters)
         */
        val regex =
            Regex("^(?:\\w{3}, )?(\\d{1,2}) (\\w{3}) (\\d{4}) (\\d{2}):(\\d{2})(?::(\\d{2}))? ([+-]?\\w+)\$")

        val result = regex.matchEntire(input)

        if (result == null || result.groups.size != 8) {
            throw IllegalArgumentException("Unexpected RFC 822 date/time format")
        }

        try {
            val dayOfMonth = result.groupValues[1].toInt()
            val month = Month.valueOf(result.groupValues[2])
            val year = result.groupValues[3].toInt()
            val hour = result.groupValues[4].toInt()
            val minute = result.groupValues[5].toInt()
            val second = result.groupValues[6].ifEmpty { "00" }.toInt()
            val timeZone = result.groupValues[7]

            val dateTime = LocalDateTime(
                year = year,
                monthNumber = month.ordinal + 1,
                dayOfMonth = dayOfMonth,
                hour = hour,
                minute = minute,
                second = second,
                nanosecond = 0,
            )

            val tz = parseTimeZone(timeZone)
            return dateTime.toInstant(tz)
        } catch (e: Exception) {
            throw IllegalArgumentException("Unexpected RFC 822 date/time format", e)
        }
    }

    /**
     * @see parse
     */
    operator fun invoke(input: String): Instant = parse(input)

    private fun parseTimeZone(timeZone: String): TimeZone {
        val startsWithPlus = timeZone.startsWith('+')
        val startsWithMinus = timeZone.startsWith('-')

        val (hours, minutes) = when {
            startsWithPlus || startsWithMinus -> {
                val hour = timeZone.substring(1..2).toInt()
                val minute = timeZone.substring(3..4).toInt()

                if (startsWithMinus) {
                    Pair(-hour, -minute)
                } else {
                    Pair(hour, minute)
                }
            }
            // Time zones
            else -> when (timeZone) {
                "Z", "UT", "GMT" -> 0.hours
                "EST" -> (-5).hours
                "EDT" -> (-4).hours
                "CST" -> (-6).hours
                "CDT" -> (-5).hours
                "MST" -> (-7).hours
                "MDT" -> (-6).hours
                "PST" -> (-8).hours
                "PDT" -> (-7).hours

                // Military
                "A" -> (-1).hours
                "M" -> (-12).hours
                "N" -> 1.hours
                "Y" -> 12.hours
                else -> throw IllegalArgumentException("Unexpected time zone format")
            }
        }

        val offset = UtcOffset(hours = hours, minutes = minutes, seconds = 0)
        return offset.asTimeZone()
    }

    private val Int.hours
        get() = Pair(this, 0)
}