package com.zhangke.utopia.status_provider

import com.zhangke.utopia.status.utils.StatusProviderUri
import org.junit.Test

class StatusProviderUriTest {

    @Test
    fun `should throw when empty host`() {
        try {
            StatusProviderUri.build("", "", emptyMap())
            assert(false)
        } catch (e: Throwable) {
            assert(true)
        }
    }

    @Test
    fun `should return uri when params is regular`() {
        val host = "activity.pub"
        val path = "user"
        val queries = mapOf("name" to "zhangke")
        val uri = StatusProviderUri.build(host, path, queries)
        assert(uri.host == host)
        assert(uri.path == "/$path")
        assert(uri.queries == queries)
    }

    @Test
    fun `should return uri when have multiple path is regular`() {
        val host = "activity.pub"
        val path = "user/name"
        val queries = mapOf("name" to "zhangke")
        val uri = StatusProviderUri.build(host, path, queries)
        assert(uri.host == host)
        assert(uri.path == "/$path")
        assert(uri.queries == queries)
    }

    @Test
    fun `should return uri when have single path and path have divider`() {
        val host = "activity.pub"
        val path = "user/"
        val queries = mapOf("name" to "zhangke")
        val uri = StatusProviderUri.build(host, path, queries)
        assert(uri.host == host)
        assert(uri.path == "/user")
        assert(uri.queries == queries)
    }

    @Test
    fun `should return uri when have multiple path and path have divider`() {
        val host = "activity.pub"
        val path = "user/name/as/"
        val queries = mapOf("name" to "zhangke")
        val uri = StatusProviderUri.build(host, path, queries)
        assert(uri.host == host)
        assert(uri.path == "/user/name/as")
        assert(uri.queries == queries)
    }

    @Test
    fun `should return null when scheme is bad`() {
        val uriString = "badScheme://activity/user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when scheme is empty`() {
        val uriString = "://activity/user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when scheme and divider is empty`() {
        val uriString = "activity/user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return uri when scheme is ok`() {
        val uriString = "statussource://activity/user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri != null)
    }

    @Test
    fun `should return null when host is empty`() {
        val uriString = "status_source:///user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when host is null`() {
        val uriString = "status_source://user?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when path is empty`() {
        val uriString = "statussource://activity_pub/?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when path is null`() {
        val uriString = "statussource://activity_pub/query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when path and divider is null`() {
        val uriString = "statussource://activity_pub?query=zhang"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return null when query is null`() {
        val uriString = "statussource://activity_pub/user"
        val uri = StatusProviderUri.create(uriString)
        assert(uri == null)
    }

    @Test
    fun `should return uri when query is empty`() {
        val uriString = "statussource://activity_pub/user?query="
        val uri = StatusProviderUri.create(uriString)
        assert(uri != null)
    }

    @Test
    fun `should return uri and empty query when query is empty`() {
        val uriString = "statussource://activity_pub/user?query="
        val uri = StatusProviderUri.create(uriString)
        assert(uri!!.host == "activity_pub")
        assert(uri.path == "/user")
        assert(uri.queries["query"] == "")
    }

    @Test
    fun `should return uri when uri is ok`() {
        val uriString = "statussource://activity_pub/user?query=zhangke"
        val uri = StatusProviderUri.create(uriString)
        assert(uri!!.host == "activity_pub")
        assert(uri.path == "/user")
        assert(uri.queries["query"] == "zhangke")
    }

    @Test
    fun `should return uri when have multiple path`() {
        val uriString = "statussource://activity_pub/user/name?query=zhangke"
        val uri = StatusProviderUri.create(uriString)
        assert(uri!!.host == "activity_pub")
        assert(uri.path == "/user/name")
        assert(uri.queries["query"] == "zhangke")
    }
}