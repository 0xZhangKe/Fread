package com.zhangke.fread.rss.internal.adapter

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RssBlogMediaExtractorTest {

    @Test
    fun extractImagesFromHtml() {
        val medias = RssBlogMediaExtractor.extract(
            itemId = "post-1",
            articleUrl = "https://example.com/posts/detail/index.html",
            contentHtml = """
                <p>Hello</p>
                <img src="/images/cover.jpg" alt="Cover" />
                <img data-src="../assets/body.png?size=large" />
                <img srcset="https://cdn.example.com/a-small.jpg 320w, https://cdn.example.com/a-large.jpg 1280w" />
            """.trimIndent(),
            descriptionHtml = null,
            fallbackImageUrl = null,
        )

        assertEquals(
            listOf(
                "https://example.com/images/cover.jpg",
                "https://example.com/posts/assets/body.png?size=large",
                "https://cdn.example.com/a-large.jpg",
            ),
            medias.map { it.url },
        )
        assertEquals("Cover", medias.first().description)
    }

    @Test
    fun ignoreLikelyIcons() {
        val medias = RssBlogMediaExtractor.extract(
            itemId = "post-2",
            articleUrl = "https://example.com/posts/detail.html",
            contentHtml = """
                <img src="https://example.com/static/avatar.png" alt="author avatar" width="48" height="48" />
                <img src="https://example.com/images/article-header.jpg" width="1200" height="630" />
            """.trimIndent(),
            descriptionHtml = null,
            fallbackImageUrl = null,
        )

        assertEquals(listOf("https://example.com/images/article-header.jpg"), medias.map { it.url })
    }

    @Test
    fun fallbackToRssImageWhenHtmlHasNoEmbeddedImage() {
        val medias = RssBlogMediaExtractor.extract(
            itemId = "post-3",
            articleUrl = "https://example.com/posts/detail.html",
            contentHtml = "<p>No image here</p>",
            descriptionHtml = null,
            fallbackImageUrl = "https://example.com/images/fallback.jpg",
        )

        assertEquals(listOf("https://example.com/images/fallback.jpg"), medias.map { it.url })
        assertNull(medias.first().description)
    }
}
