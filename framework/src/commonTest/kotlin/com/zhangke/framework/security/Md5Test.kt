package com.zhangke.framework.security

import kotlin.test.Test
import kotlin.test.assertEquals

class Md5Test {
    @Test
    fun testMd5() {
        val md5 = Md5.md5("123456")
        assertEquals("e10adc3949ba59abbe56e057f20f883e", md5)
    }
}