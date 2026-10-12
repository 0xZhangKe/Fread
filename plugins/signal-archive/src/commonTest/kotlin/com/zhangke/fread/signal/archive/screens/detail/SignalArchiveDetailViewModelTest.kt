package com.zhangke.fread.signal.archive.screens.detail

import com.zhangke.fread.signal.archive.model.SignalArchiveArticle
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignalArchiveDetailViewModelTest {
    @BeforeTest
    fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }

    @AfterTest
    fun teardown() { Dispatchers.resetMain() }

    @Test
    fun retriesDetailPathAndLoadsArticlesWithoutOriginalLinks() = runTest {
        var requests = 0
        val client = HttpClient(MockEngine { request ->
            assertEquals("https://example.com/data/articles/custom.json", request.url.toString())
            if (++requests == 1) respond("", HttpStatusCode.NotFound) else respond("""
                {"id":"article","title":"AI article","summary":"Summary","content":"<p>Body</p>",
                 "sourceId":"source","sourceTitle":"AI","link":null,"image":null,"publishedAt":null}
            """.trimIndent())
        })
        try {
            val vm = SignalArchiveDetailViewModel(
                SignalArchiveDetailNavKey("https://example.com/data/", "articles/custom.json", "Title"),
                SignalArchiveRepo(client, Json),
            )
            assertTrue(vm.uiState.value.loading)
            vm.load()
            advanceUntilIdle()
            assertEquals(SignalArchiveDetailUiState(failed = true), vm.uiState.value)
            vm.load()
            vm.load()
            advanceUntilIdle()
            assertEquals(SignalArchiveDetailUiState(article = SignalArchiveArticle(
                "article", "AI article", "Summary", "<p>Body</p>", "source", "AI",
            )), vm.uiState.value)
            assertEquals(2, requests)
        } finally { client.close() }
    }

    @Test
    fun documentEscapesMetadataAndPreservesBodyMarkup() {
        val article = SignalArchiveArticle(
            "article", "<script>alert(1)</script>", "Summary", "<p>Body <strong>bold</strong></p>",
            "source", "A & B",
        )
        val document = signalArchiveArticleDocument(article, 0xFF123456.toInt(), 0xFFFFFFFF.toInt(), 0xFF0000FF.toInt())
        assertTrue(document.contains("<h1>&lt;script&gt;alert(1)&lt;/script&gt;</h1>"))
        assertTrue(document.contains("A &amp; B"))
        assertTrue(document.contains("<p>Body <strong>bold</strong></p>"))
        assertTrue(document.contains("color: #123456"))
    }
}
