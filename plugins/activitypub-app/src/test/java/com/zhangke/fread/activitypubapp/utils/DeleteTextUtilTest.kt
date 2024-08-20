package com.zhangke.fread.activitypubapp.utils

import com.zhangke.fread.activitypub.app.internal.utils.findBeforeEmoji
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeleteTextUtilTest {

    @Test
    fun testSingleEmoji() {
        val text = " :123: "
        val expect = IntRange(0, text.length)
        assertEquals(expect, findBeforeEmoji(text, text.length))
        assertNull(findBeforeEmoji(text, 0))
        assertNull(findBeforeEmoji(text, text.length - 1))
        assertNull(findBeforeEmoji(text, text.length + 1))
        assertNull(findBeforeEmoji(text, 3))
    }

    @Test
    fun testWholeStartSentence() {
        val text = "qw :123: "
        val expect = IntRange(2, text.length)
        assertEquals(expect, findBeforeEmoji(text, text.length))
        assertNull(findBeforeEmoji(text, 0))
        assertNull(findBeforeEmoji(text, text.length - 1))
        assertNull(findBeforeEmoji(text, text.length + 1))
        assertNull(findBeforeEmoji(text, 3))
    }

    @Test
    fun testWholeEndSentence() {
        val text = " :123: as"
        val expect = IntRange(0, text.length - 2)
        assertEquals(expect, findBeforeEmoji(text, text.length - 2))
        assertNull(findBeforeEmoji(text, 0))
        assertNull(findBeforeEmoji(text, text.length - 1))
        assertNull(findBeforeEmoji(text, text.length + 1))
        assertNull(findBeforeEmoji(text, 3))
    }

    @Test
    fun testWholeSentence() {
        val text = "qw :123: as"
        val expect = IntRange(2, text.length - 2)
        assertEquals(expect, findBeforeEmoji(text, text.length - 2))
        assertNull(findBeforeEmoji(text, 0))
        assertNull(findBeforeEmoji(text, text.length - 1))
        assertNull(findBeforeEmoji(text, text.length + 1))
        assertNull(findBeforeEmoji(text, 3))
    }
}
