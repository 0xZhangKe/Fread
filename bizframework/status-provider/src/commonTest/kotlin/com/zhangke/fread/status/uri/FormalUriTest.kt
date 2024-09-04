package com.zhangke.fread.status.uri

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class FormalUriTest {

    @Test
    fun `should throw when empty host`() {
        try {
            FormalUri.create("", "", emptyMap())
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    @Test
    fun `should return uri when params is regular`() {
        val host = "activity.pub"
        val path = "user"
        val queries = mapOf("name" to "zhangke")
        val uri = FormalUri.create(host, path, queries)
        assertEquals(uri.host, host)
        assertEquals(uri.path, "/$path")
        assertEquals(uri.queries, queries)
    }

    @Test
    fun `should return uri when have multiple path is regular`() {
        val host = "activity.pub"
        val path = "user/name"
        val queries = mapOf("name" to "zhangke")
        val uri = FormalUri.create(host, path, queries)
        assertEquals(uri.host, host)
        assertEquals(uri.path, "/$path")
        assertEquals(uri.queries, queries)
    }

    @Test
    fun `should return uri when have single path and path have divider`() {
        val host = "activity.pub"
        val path = "user/"
        val queries = mapOf("name" to "zhangke")
        val uri = FormalUri.create(host, path, queries)
        assertEquals(uri.host, host)
        assertEquals(uri.path, "/user")
        assertEquals(uri.queries, queries)
    }

    @Test
    fun `should return uri when have multiple path and path have divider`() {
        val host = "activity.pub"
        val path = "user/name/as/"
        val queries = mapOf("name" to "zhangke")
        val uri = FormalUri.create(host, path, queries)
        assertEquals(uri.host, host)
        assertEquals(uri.path, "/user/name/as")
        assertEquals(uri.queries, queries)
    }

    @Test
    fun `should return null when scheme is bad`() {
        val uriString = "badScheme://activity/user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when scheme is empty`() {
        val uriString = "://activity/user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when scheme and divider is empty`() {
        val uriString = "activity/user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return uri when scheme is ok`() {
        val uriString = "${FormalUri.SCHEME}://activity/user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertTrue(uri != null)
    }

    @Test
    fun `should return null when host is empty`() {
        val uriString = "${FormalUri.SCHEME}:///user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when host is null`() {
        val uriString = "${FormalUri.SCHEME}://user?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when path is empty`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when path is null`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when path and divider is null`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub?query=zhang"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return null when query is null`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/user"
        val uri = FormalUri.from(uriString)
        assertEquals(uri, null)
    }

    @Test
    fun `should return uri when query is empty`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/user?query="
        val uri = FormalUri.from(uriString)
        assertTrue(uri != null)
    }

    @Test
    fun `should return uri and empty query when query is empty`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/user?query="
        val uri = FormalUri.from(uriString)
        assertEquals(uri!!.host, "activity_pub")
        assertEquals(uri.path, "/user")
        assertEquals(uri.queries["query"], "")
    }

    @Test
    fun `should return uri when uri is ok`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/user?query=zhangke"
        val uri = FormalUri.from(uriString)
        assertEquals(uri!!.host, "activity_pub")
        assertEquals(uri.path, "/user")
        assertEquals(uri.queries["query"], "zhangke")
    }

    @Test
    fun `should return uri when have multiple path`() {
        val uriString = "${FormalUri.SCHEME}://activity_pub/user/name?query=zhangke"
        val uri = FormalUri.from(uriString)
        assertEquals(uri!!.host, "activity_pub")
        assertEquals(uri.path, "/user/name")
        assertEquals(uri.queries["query"], "zhangke")
    }
}