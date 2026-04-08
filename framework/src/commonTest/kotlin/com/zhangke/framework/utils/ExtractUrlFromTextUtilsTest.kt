package com.zhangke.framework.utils

import kotlin.test.Test
import kotlin.test.assertContentEquals

class ExtractUrlFromTextUtilsTest {

    @Test
    fun `should extract urls from text`() {
        assertContentEquals(
            expected = listOf(
                "https://example.com/path?a=1",
                "example.org/hello",
            ),
            actual = ExtractUrlFromTextUtils.extract(
                "Visit https://example.com/path?a=1 and example.org/hello now."
            ),
        )
    }

    @Test
    fun `should trim trailing punctuation`() {
        assertContentEquals(
            expected = listOf(
                "https://example.com/path(test)",
                "https://another.example.com/demo",
                "https://third.example.com",
            ),
            actual = ExtractUrlFromTextUtils.extract(
                "(https://example.com/path(test)), https://another.example.com/demo. [https://third.example.com]"
            ),
        )
    }

    @Test
    fun `should ignore invalid urls`() {
        assertContentEquals(
            expected = emptyList(),
            actual = ExtractUrlFromTextUtils.extract(
                "localhost http://-example.com example invalid_domain"
            ),
        )
    }
}
