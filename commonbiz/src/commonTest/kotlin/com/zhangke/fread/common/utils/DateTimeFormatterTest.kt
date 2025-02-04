package com.zhangke.fread.common.utils

import com.zhangke.fread.status.utils.DateTimeFormatter
import com.zhangke.fread.status.utils.DatetimeFormatConfig
import kotlinx.datetime.Clock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class DateTimeFormatterTest {

    private lateinit var config: DatetimeFormatConfig

    @BeforeTest
    fun initConfig() {
        config = DatetimeFormatConfig(
            day = "天",
            hour = "小时",
            minutes = "分钟",
            second = "秒",
            ago = "前",
        )
    }

    @Test
    fun testFormat() {
        val datetime = Clock.System.now()
        assertEquals("10 秒前", DateTimeFormatter.format(datetime.minus(10.seconds).toEpochMilliseconds(), config))
        assertEquals("10 分钟前", DateTimeFormatter.format(datetime.minus(10.minutes).toEpochMilliseconds(), config))
        assertEquals("10 小时前", DateTimeFormatter.format(datetime.minus(10.hours).toEpochMilliseconds(), config))
        assertEquals("1 天前", DateTimeFormatter.format(datetime.minus(1.days).toEpochMilliseconds(), config))
        assertEquals("2 天前", DateTimeFormatter.format(datetime.minus(2.days).toEpochMilliseconds(), config))
        assertEquals("2 天前", DateTimeFormatter.format(datetime.minus(2.9.days).toEpochMilliseconds(), config))
        assertEquals("2024-09-13", DateTimeFormatter.format(1726201813038, config))
    }
}