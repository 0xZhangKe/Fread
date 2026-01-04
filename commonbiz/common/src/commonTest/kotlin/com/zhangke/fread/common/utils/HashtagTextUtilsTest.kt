package com.zhangke.fread.common.utils

import kotlin.test.Test
import kotlin.test.assertContentEquals
import androidx.compose.ui.text.TextRange

class HashtagTextUtilsTest {
    @Test
    fun testMastodonHashtags() {
        // for Mastodon hashtag behavior see:
        // https://github.com/mastodon/mastodon/blob/e89acc2302df49cbd7815b031e9c2939632bd204/app/javascript/mastodon/utils/hashtags.ts
        assertContentEquals(
            HashtagTextUtils.findHashtags("abc"),
            listOf()
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("#abc"),
            listOf(
                TextRange(0, 4),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("#abc#def"),
            listOf(
                TextRange(0, 4),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("#abc #def"), listOf(
                TextRange(0, 4),
                TextRange(5, 9),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("##abc"), listOf(
                TextRange(1, 5),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("#a##abc"), listOf(
                TextRange(0, 2),
                TextRange(3, 7),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("#a!#b_c!##d"), listOf(
                TextRange(0, 2),
                TextRange(3, 7),
                TextRange(9, 11),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("foo #"),
            listOf()
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("foo #_"),
            listOf()
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("# a"),
            listOf()
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("(#a)"),
            listOf(
                TextRange(1, 3),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("()#a"),
            listOf()
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags(")(#a"),
            listOf(
                TextRange(2, 4),
            )
        )
        assertContentEquals(
            HashtagTextUtils.findHashtags("/#a"),
            listOf()
        )
        /*
        // this will fail. the Mastodon client will process this as a hashtag, but this seems
        // like fairly obscure or even unintended behavior
        assertContentEquals(
            HashtagTextUtils.findHashtags("#___"), listOf(
                TextRange(0, 4),
            )
        )*/
    }
}
