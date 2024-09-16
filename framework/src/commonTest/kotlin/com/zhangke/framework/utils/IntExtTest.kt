package com.zhangke.framework.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class IntExtTest {
    @Test
    fun testIntFormatAsCount() {
        assertEquals("1M", 1_000_000.formatToHumanReadable())
        assertEquals("1.1M", 1_100_000.formatToHumanReadable())
        assertEquals("1.9M", 1_900_000.formatToHumanReadable())
        assertEquals("1.5M", 1_500_000.formatToHumanReadable())
        assertEquals("1M", 1_009_000.formatToHumanReadable())
        assertEquals("1.1M", 1_090_000.formatToHumanReadable())
        assertEquals("10M", 10_000_000.formatToHumanReadable())
        assertEquals("1K", 1000.formatToHumanReadable())
        assertEquals("10K", 10000.formatToHumanReadable())
        assertEquals("89K", 89000.formatToHumanReadable())
        assertEquals("99K", 99000.formatToHumanReadable())
        assertEquals("999", 999.formatToHumanReadable())
    }
}
