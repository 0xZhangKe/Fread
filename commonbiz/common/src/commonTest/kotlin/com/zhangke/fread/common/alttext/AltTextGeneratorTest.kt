package com.zhangke.fread.common.alttext

import kotlin.test.Test
import kotlin.test.assertEquals

class AltTextGeneratorTest {

    @Test
    fun stripsThinkingTextWhenFinalTextFollowsClosingTag() {
        val response = """
            Analyze the image step by step.
            </think>

            A sunlit yellow tree surrounded by autumn foliage.
        """.trimIndent()

        assertEquals(
            expected = "A sunlit yellow tree surrounded by autumn foliage.",
            actual = response.stripThinkingText(),
        )
    }

    @Test
    fun keepsOriginalTextWhenNothingFollowsClosingTag() {
        val response = "Analyze the image.</think>   "

        assertEquals(
            expected = response,
            actual = response.stripThinkingText(),
        )
    }

    @Test
    fun keepsOriginalTextWithoutClosingTag() {
        val response = "A sunlit yellow tree surrounded by autumn foliage."

        assertEquals(
            expected = response,
            actual = response.stripThinkingText(),
        )
    }
}
