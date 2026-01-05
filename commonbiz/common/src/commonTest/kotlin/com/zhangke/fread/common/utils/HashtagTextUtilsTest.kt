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
            listOf(),
            HashtagTextUtils.findHashtags("abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils.findHashtags("#abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils.findHashtags("#abc#def")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
                TextRange(5, 9),
            ),
            HashtagTextUtils.findHashtags("#abc #def")
        )
        assertContentEquals(
            listOf(
                TextRange(1, 5),
            ),
            HashtagTextUtils.findHashtags("##abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 2),
                TextRange(3, 7),
            ),
            HashtagTextUtils.findHashtags("#a##abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 2),
                TextRange(3, 7),
                TextRange(9, 11),
            ),
            HashtagTextUtils.findHashtags("#a!#b_c!##d")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils.findHashtags("foo #")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils.findHashtags("foo #_")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils.findHashtags("# a")
        )
        assertContentEquals(
            listOf(
                TextRange(1, 3),
            ),
            HashtagTextUtils.findHashtags("(#a)")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils.findHashtags("()#a")
        )
        assertContentEquals(
            listOf(
                TextRange(2, 4),
            ),
            HashtagTextUtils.findHashtags(")(#a")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils.findHashtags("/#a")
        )
        /*
        // this will fail. the Mastodon client will process this as a hashtag, but this seems
        // like fairly obscure or even unintended behavior
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils.findHashtags("#___")
        )*/
    }

    @Test
    fun testBlueskyHashtags() {
        // for Bluesky hashtag behavior see:
        // https://github.com/bluesky-social/atproto/blob/3cf5b31a2d8194dcfbfb8c3cc8e61282e48c9a82/packages/api/src/rich-text/util.ts#L10-L12
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("abc"),
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils2.findHashtags("#abc"),
        )
        assertContentEquals(
            listOf(
                TextRange(0, 8),
            ),
            HashtagTextUtils2.findHashtags("#abc#def")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
                TextRange(5, 9),
            ),
            HashtagTextUtils2.findHashtags("#abc #def")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 5),
            ),
            HashtagTextUtils2.findHashtags("##abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 7),
            ),
            HashtagTextUtils2.findHashtags("#a##abc")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 11),
            ),
            HashtagTextUtils2.findHashtags("#a!#b_c!##d")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("foo #")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("foo #_")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("# a")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("(#a)")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("()#a")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags(")(#a")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("/#a")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 2),
            ),
            HashtagTextUtils2.findHashtags("#a! b")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 2),
            ),
            HashtagTextUtils2.findHashtags("#a!")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils2.findHashtags("#a!b")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 6),
            ),
            HashtagTextUtils2.findHashtags("#a!b!c")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 6),
            ),
            HashtagTextUtils2.findHashtags("#a!b!c!")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 5),
            ),
            HashtagTextUtils2.findHashtags("#a!!b")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("#!! ")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4),
            ),
            HashtagTextUtils2.findHashtags("#!!a")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("#")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 4)
            ),
            HashtagTextUtils2.findHashtags("#a=b")
        )
        assertContentEquals(
            listOf(
                TextRange(0, 2)
            ),
            HashtagTextUtils2.findHashtags("#=")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("a#b")
        )
        assertContentEquals(
            listOf(),
            HashtagTextUtils2.findHashtags("a#b c")
        )
    }
}
