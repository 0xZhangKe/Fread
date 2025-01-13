package com.zhangke.fread.status.richtext.parser

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.text.buildAnnotatedString
import com.zhangke.fread.status.model.Emoji
import kotlin.test.Test
import kotlin.test.assertEquals

class HtmlParserTest {

    @Test
    fun testAppendWithEmoji() {
        val html = "Yeah, you can disable comments on your :pixelfed: posts"
        val emojis = mapOf(
            "pixelfed" to Emoji("pixelfed", "https://files.mastodon.social/custom_emojis/images/000/068/773/static/pixelfed-icon-color.png", ""),
        )
        assertEquals(
            buildAnnotatedString {
                appendWithEmoji(
                    html,
                    emojis,
                )
            },
            buildAnnotatedString {
                append("Yeah, you can disable comments on your ")
                appendInlineContent("emoji", "https://files.mastodon.social/custom_emojis/images/000/068/773/static/pixelfed-icon-color.png")
                append(" posts")
            }
        )
    }
}