package com.zhangke.framework.network

import org.junit.Assert
import org.junit.Test

class SimpleUriTest {

    @Test
    fun `should return null when empty`() {
        Assert.assertNull(SimpleUri.parse(""))
    }

    @Test
    fun `should return host when given host`() {
        val host = "example.com"
        val uri = SimpleUri.parse("https://$host")
        Assert.assertEquals(host, uri!!.host)
    }

    @Test
    fun `should return scheme when given scheme`() {
        val scheme = "ftp"
        val uri = SimpleUri.parse("${scheme}://example.com")
        Assert.assertEquals(scheme, uri!!.scheme)
    }

    @Test
    fun `should has path when given path`() {
        val uri = SimpleUri.parse("http://example.com/a/b/c?name=we")
        Assert.assertEquals("/a/b/c", uri!!.path)
    }

    @Test
    fun `should return query when given path is empty`() {
        val uri = SimpleUri.parse("https://a.b/?a=1&b=2")
        Assert.assertEquals("1", uri!!.queries["a"])
        Assert.assertEquals("2", uri.queries["b"])
    }

    @Test
    fun `should return query when given query`() {
        val uri = SimpleUri.parse("https://a.b/path?a=1&b=2")
        Assert.assertEquals("1", uri!!.queries["a"])
        Assert.assertEquals("2", uri.queries["b"])
    }
}
