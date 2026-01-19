package com.zhangke.framework.date

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import org.junit.Assert.assertEquals
import org.junit.Test

class DateParserTest {

    @Test
    fun parseISODate() {
        assertEquals(
            LocalDateTime(2021, 9, 1, 12, 0, 0)
                .toInstant(TimeZone.UTC)
                .toJavaInstant(),
            DateParser.parseISODate("2021-09-01T12:00:00Z"),
        )
    }

    @Test
    fun parseRfc822Date() {
        assertEquals(
            LocalDateTime(2021, 9, 1, 12, 0, 0)
                .toInstant(TimeZone.UTC)
                .toJavaInstant(),
            DateParser.parseRfc822Date("Wed, 01 Sep 2021 12:00:00 GMT"),
        )
    }

    @Test
    fun parseRfc3339Date() {
        assertEquals(
            LocalDateTime(2020, 7, 31, 9, 16, 15)
                .toInstant(UtcOffset(2))
                .toJavaInstant(),
            DateParser.parseRfc3339Date("2020-07-31T09:16:15+02:00"),
        )
    }

    @Test
    fun parseISO8601() {
        assertEquals(
            LocalDateTime(2021, 9, 1, 12, 0, 0)
                .toInstant(TimeZone.UTC)
                .toJavaInstant(),
            DateParser.parseISO8601("2021-09-01T12:00:00Z"),
        )
    }
}