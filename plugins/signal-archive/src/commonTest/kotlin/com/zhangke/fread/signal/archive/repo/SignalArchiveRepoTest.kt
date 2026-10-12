package com.zhangke.fread.signal.archive.repo

import com.zhangke.fread.signal.archive.model.SignalArchiveArticle
import com.zhangke.fread.signal.archive.model.SignalArchiveArticleSummary
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.model.SignalArchiveNode
import com.zhangke.fread.signal.archive.model.SignalArchiveNodeType
import com.zhangke.fread.signal.archive.model.SignalArchivePage
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SignalArchiveRepoTest {

    @Test
    fun readsIndexPageAndArticleFromServerPaths() = runTest {
        val requestedPaths = mutableListOf<String>()
        val client = HttpClient(MockEngine { request ->
            val path = request.url.encodedPath
            requestedPaths += path
            respond(when (path) {
                "/data/rendered.json" -> """
                    {"startedAt":"2026-09-24T00:20:07Z","futureField":true,"children":[
                      {"type":"category","title":"News","articleCount":1,"failed_count":0,
                       "pages":["categories/custom.json"],"children":[
                        {"type":"rss","title":"RSS","articleCount":1,"state":1.0,"pages":[],"originalUrl":null},
                        {"type":"ai","title":"AI","articleCount":0,"state":0.5,"pages":[]}
                       ]}
                    ]}
                """.trimIndent()
                "/data/categories/custom.json" -> """
                    {"items":[{"id":"a","title":"Title","summary":"Summary",
                     "sourceId":"s","sourceTitle":"RSS","detailPath":"articles/custom.json",
                     "image":null,"publishedAt":null,"link":null}]}
                """.trimIndent()
                "/data/articles/custom.json" -> """
                    {"id":"a","title":"Title","summary":"Summary","content":"<p>Body</p>",
                     "sourceId":"s","sourceTitle":"RSS"}
                """.trimIndent()
                else -> error("Unexpected request: $path")
            })
        })
        try {
            val repo = SignalArchiveRepo(client)
            val index = repo.getIndex("https://example.com/data/").getOrThrow()
            assertEquals(
                SignalArchiveIndex(
                    startedAt = "2026-09-24T00:20:07Z",
                    children = listOf(SignalArchiveNode(
                        type = SignalArchiveNodeType.CATEGORY,
                        title = "News",
                        articleCount = 1,
                        pages = listOf("categories/custom.json"),
                        failedCount = 0,
                        children = listOf(
                            SignalArchiveNode(SignalArchiveNodeType.RSS, "RSS", 1, emptyList(), state = 1.0),
                            SignalArchiveNode(SignalArchiveNodeType.AI, "AI", 0, emptyList(), state = 0.5),
                        ),
                    )),
                ),
                index,
            )
            val page = repo.getPage("https://example.com/data", index.children.single().pages.single()).getOrThrow()
            assertEquals(SignalArchivePage(listOf(
                SignalArchiveArticleSummary("a", "Title", "Summary", "s", "RSS", "articles/custom.json"),
            )), page)
            assertEquals(
                SignalArchiveArticle("a", "Title", "Summary", "<p>Body</p>", "s", "RSS"),
                repo.getArticle("https://example.com/data/", page.items.single().detailPath).getOrThrow(),
            )
            assertEquals(listOf("/data/rendered.json", "/data/categories/custom.json", "/data/articles/custom.json"), requestedPaths)
        } finally {
            client.close()
        }
    }

    @Test
    fun preservesHttpErrorsAndRejectsMalformedJson() = runTest {
        for (status in listOf(304, 404, 429, 503)) {
            val client = HttpClient(MockEngine { respond("", HttpStatusCode.fromValue(status)) })
            try {
                val failure = SignalArchiveRepo(client).getIndex("https://example.com/data").exceptionOrNull()
                assertEquals(status, assertIs<SignalArchiveHttpException>(failure).statusCode)
            } finally {
                client.close()
            }
        }
        val client = HttpClient(MockEngine { respond("{}") })
        try {
            assertTrue(SignalArchiveRepo(client).getIndex("https://example.com/data").isFailure)
        } finally {
            client.close()
        }
    }

    @Test
    fun rejectsPathsOutsideDataDirectoryBeforeRequesting() = runTest {
        val client = HttpClient(MockEngine { error("Must not request invalid paths") })
        try {
            val repo = SignalArchiveRepo(client)
            for (path in listOf("", "../secret.json", "/page.json", "https://other.com/page.json", "%2e%2e/page.json", "page.json?q=1")) {
                assertIs<IllegalArgumentException>(repo.getPage("https://example.com/data", path).exceptionOrNull())
            }
            assertIs<IllegalArgumentException>(repo.getIndex("file:///data").exceptionOrNull())
        } finally {
            client.close()
        }
    }

    @Test
    fun propagatesCancellation() = runTest {
        val client = HttpClient(MockEngine { throw CancellationException("Cancelled") })
        try {
            assertFailsWith<CancellationException> {
                SignalArchiveRepo(client).getIndex("https://example.com/data")
            }
        } finally {
            client.close()
        }
    }
}
