package com.zhangke.framework.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class IntExtTest {
    @Test
    fun testIntFormatAsCount() {
        assertEquals("1.2GB", (1.2 * 1024 * 1024 * 1024).toInt().formatAsCount())
        assertEquals("22.2MB", (22.2 * 1024 * 1024).toInt().formatAsCount())
        assertEquals("33.3KB", (33.33 * 1024).toInt().formatAsCount())
        assertEquals("33.4KB", (33.37 * 1024).toInt().formatAsCount())
        assertEquals("44B", (44.44).toInt().formatAsCount())
    }
}