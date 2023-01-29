package com.zhangke.utopia.activitypubapp.utils

import org.junit.Test

internal class WebFingerTest {
    /**
     * Supported:
     * - jw@jakewharton.com
     * - @jw@jakewharton.com
     * - acct:@jw@jakewharton.com
     * - https://m.cmx.im/@jw@jakewharton.com
     * - https://m.cmx.im/@AtomZ
     * - m.cmx.im/@jw@jakewharton.com
     * - jakewharton.com/@jw
     */
    @Test
    fun value() {
        println(WebFinger("").value)
        println(WebFinger("Supported").value)
        println(WebFinger("jw@jakewharton.com").value)
        println(WebFinger("@jw@jakewharton.com").value)
        println(WebFinger("@@jw@jakewharton.com").value)
        println(WebFinger("@@jw@@jakewharton.com").value)
        println(WebFinger("jakewharton.com").value)
    }
}