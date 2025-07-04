package com.zhangke.framework.utils

import com.zhangke.framework.network.FormalBaseUrl
import org.junit.Assert
import org.junit.Test

/**
 * Supported:
 * - jw@jakewharton.com
 * - @jw@jakewharton.com
 * - acct:@jw@jakewharton.com
 * - https://m.cmx.im/@jw@jakewharton.com
 * - https://m.cmx.im/@AtomZ
 * - https://m.cmx.im/xxx/AtomZ
 * - m.cmx.im/@jw@jakewharton.com
 * - jakewharton.com/@jw
 */
internal class WebFingerTest {

    @Test
    fun `should return null when content is empty`() {
        assert(WebFinger.create("") == null)
    }

    @Test
    fun `should return null when content is not a web finger`() {
        assert(WebFinger.create("Supported") == null)
    }

    @Test
    fun `should return WebFinger when acct and base url is present`() {
        val baseUrl = FormalBaseUrl.parse("https://m.cmx.im")!!
        val webFinger = WebFinger.create("@jw@jakewharton.com", baseUrl)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when acct without @ and base url is present`() {
        val baseUrl = FormalBaseUrl.parse("https://m.cmx.im")!!
        val webFinger = WebFinger.create("jw@jakewharton.com", baseUrl)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when acct just a name and base url is present`() {
        val baseUrl = FormalBaseUrl.parse("https://m.cmx.im")!!
        val webFinger = WebFinger.create("@jw", baseUrl)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "m.cmx.im")
    }

    @Test
    fun `should return WebFinger when acct just a name without @ and base url is present`() {
        val baseUrl = FormalBaseUrl.parse("https://m.cmx.im")!!
        val webFinger = WebFinger.create("jw", baseUrl)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "m.cmx.im")
    }

    @Test
    fun `should return WebFinger when content prefix not @`() {
        val webFinger = WebFinger.create("jw@jakewharton.com")
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when content is standard`() {
        val webFinger = WebFinger.create("@jw@jakewharton.com")
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return null when content is have two @ prefix`() {
        assert(WebFinger.create("@@jw@jakewharton.com") == null)
    }

    @Test
    fun `should return null when content is have two @ prefix and two @@ on domain`() {
        assert(WebFinger.create("@@jw@@jakewharton.com") == null)
    }

    @Test
    fun `should return null when content is domain`() {
        assert(WebFinger.create("jakewharton.com") == null)
    }

    @Test
    fun `should return WebFinger when content have acct`() {
        assert(WebFinger.create("acct:@jw@jakewharton.com") != null)
    }

    @Test
    fun `should return WebFinger when content is url`() {
        // https://m.cmx.im/@jw@jakewharton.com
        val webFinger = WebFinger.create("https://m.cmx.im/@jw@jakewharton.com")
        assert(webFinger != null)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when url path no domain`() {
        val webFinger = WebFinger.create("https://m.cmx.im/@AtomZ")
        assert(webFinger != null)
        assert(webFinger!!.name == "AtomZ")
        assert(webFinger.host == "m.cmx.im")
    }

    @Test
    fun `should return WebFinger when url path no protocol`() {
        val webFinger = WebFinger.create("m.cmx.im/@jw@jakewharton.com")
        assert(webFinger != null)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when url path no protocol no domain`() {
        val webFinger = WebFinger.create("jakewharton.com/@jw")
        assert(webFinger != null)
        assert(webFinger!!.name == "jw")
        assert(webFinger.host == "jakewharton.com")
    }

    @Test
    fun `should return WebFinger when url container - symbol`() {
        val webFinger = WebFinger.create("https://ramen-fsm.eu.org/@midX")!!
        Assert.assertEquals("@midX@ramen-fsm.eu.org", webFinger.toString())
    }
}