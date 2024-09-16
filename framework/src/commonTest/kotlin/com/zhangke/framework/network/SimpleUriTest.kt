package com.zhangke.framework.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SimpleUriTest {

    @Test
    fun `should return null when empty`() {
        assertNull(SimpleUri.parse(""))
    }

    @Test
    fun `should return host when given host`() {
        val host = "example.com"
        val uri = SimpleUri.parse("https://$host")
        assertEquals(host, uri!!.host)
    }

    @Test
    fun `should return scheme when given scheme`() {
        val scheme = "ftp"
        val uri = SimpleUri.parse("${scheme}://example.com")
        assertEquals(scheme, uri!!.scheme)
    }

    @Test
    fun `should has path when given path`() {
        val uri = SimpleUri.parse("http://example.com/a/b/c?name=we")
        assertEquals("/a/b/c", uri!!.path)
    }

    @Test
    fun `should return query when given path is empty`() {
        val uri = SimpleUri.parse("https://a.b/?a=1&b=2")
        assertEquals("1", uri!!.queries["a"])
        assertEquals("2", uri.queries["b"])
    }

    @Test
    fun `should return query when given query`() {
        val uri = SimpleUri.parse("https://a.b/path?a=1&b=2")
        assertEquals("1", uri!!.queries["a"])
        assertEquals("2", uri.queries["b"])
    }

    @Test
    fun testParse() {
        val simpleUri = SimpleUri.parse("fread://activitypub/platform/detail?baseUrl=https://mastodon.social")
        assertNotNull(simpleUri)
        assertEquals("fread", simpleUri.scheme)
        assertEquals("activitypub", simpleUri.host)
        assertEquals("/platform/detail", simpleUri.path)
        assertEquals("https://mastodon.social", simpleUri.queries["baseUrl"])
    }
}