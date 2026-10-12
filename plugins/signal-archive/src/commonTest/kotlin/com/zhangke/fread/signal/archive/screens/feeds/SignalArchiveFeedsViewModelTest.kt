package com.zhangke.fread.signal.archive.screens.feeds

import com.zhangke.fread.signal.archive.model.SignalArchiveArticleSummary
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.model.SignalArchiveNode
import com.zhangke.fread.signal.archive.model.SignalArchiveNodeType
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SignalArchiveFeedsViewModelTest {
    private val clients = mutableListOf<HttpClient>()

    @BeforeTest
    fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }

    @AfterTest
    fun teardown() {
        clients.forEach { it.close() }
        Dispatchers.resetMain()
    }

    @Test
    fun loadsOnlyItsNodeAndRetriesSamePageWithoutDuplicatingArticles() = runTest {
        val paths = mutableListOf<String>()
        var attempts = 0
        val vm = createViewModel { path ->
            paths += path
            when (path) {
                "/data/first.json" -> page("a", "b") to 200
                "/data/second.json" -> if (++attempts == 1) "" to 503 else page("b", "c") to 200
                else -> error(path)
            }
        }
        advanceUntilIdle()
        assertTrue(paths.isEmpty())
        vm.ensureLoaded()
        advanceUntilIdle()
        vm.loadMore()
        vm.loadMore()
        advanceUntilIdle()
        assertEquals(SignalArchiveFeedsUiState(listOf(article("a"), article("b")), nextPage = 1, failed = true), vm.uiState.value)
        vm.loadMore()
        advanceUntilIdle()
        vm.loadMore()
        assertEquals(SignalArchiveFeedsUiState(listOf(article("a"), article("b"), article("c")), nextPage = 2), vm.uiState.value)
        assertEquals(listOf("/data/first.json", "/data/second.json", "/data/second.json"), paths)
    }

    @Test
    fun newTabSnapshotCancelsOldRequestAndResetsCursor() = runTest {
        val blocked = CompletableDeferred<Unit>()
        val vm = createViewModel { path ->
            when (path) {
                "/data/first.json" -> { blocked.await(); page("old") to 200 }
                "/data/new.json" -> page("new") to 200
                else -> error(path)
            }
        }
        vm.ensureLoaded()
        runCurrent()
        val newInfo = INFO.copy(node = INFO.node.copy(pages = listOf("new.json")), generation = 2)
        vm.updateTab(newInfo)
        vm.ensureLoaded()
        advanceUntilIdle()
        blocked.complete(Unit)
        advanceUntilIdle()
        assertEquals(SignalArchiveFeedsUiState(listOf(article("new")), nextPage = 1), vm.uiState.value)
        vm.updateTab(newInfo)
        assertEquals(1, vm.uiState.value.nextPage)
    }

    @Test
    fun deploymentRecoveryPublishesIndexAndIsBounded() = runTest {
        var indexes = 0
        val vm = createViewModel { path ->
            if (path == "/data/rendered.json") {
                indexes++
                """{"children":[{"type":"category","title":"News","articleCount":1,"pages":["missing.json"]}]}""" to 200
            } else "" to 404
        }
        val indexEvents = mutableListOf<SignalArchiveIndex>()
        backgroundScope.launch {
            vm.indexUpdates.collect { index -> indexEvents += index; vm.acceptIndex(index) }
        }
        vm.ensureLoaded()
        advanceUntilIdle()
        runCurrent()
        advanceUntilIdle()
        assertEquals(1, indexes)
        assertEquals(1, indexEvents.size)
        assertTrue(vm.uiState.value.failed)
    }

    @Test
    fun failedIndexRefreshKeepsItemsAndCanBeRetried() = runTest {
        val vm = createViewModel { path ->
            if (path.endsWith("rendered.json")) "" to 503 else page("a") to 200
        }
        vm.ensureLoaded()
        advanceUntilIdle()
        vm.refresh()
        advanceUntilIdle()
        assertEquals(SignalArchiveFeedsUiState(listOf(article("a")), nextPage = 1, failed = true, indexFailed = true), vm.uiState.value)
    }

    private fun createViewModel(response: suspend (String) -> Pair<String, Int>): SignalArchiveFeedsViewModel {
        val client = HttpClient(MockEngine { request ->
            val (body, code) = response(request.url.encodedPath)
            respond(body, HttpStatusCode.fromValue(code))
        })
        clients += client
        return SignalArchiveFeedsViewModel(INFO, SignalArchiveRepo(client, Json))
    }

    private fun page(vararg ids: String): String {
        val items = ids.joinToString(",") { id ->
            """{"id":"$id","title":"$id","summary":"Summary","sourceId":"s","sourceTitle":"Source","detailPath":"articles/$id.json"}"""
        }
        return """{"items":[$items]}"""
    }

    private fun article(id: String) = SignalArchiveArticleSummary(id, id, "Summary", "s", "Source", "articles/$id.json")

    companion object {
        private val INFO = SignalArchiveFeedsTabInfo(
            "archive", "https://example.com/data/", 0,
            SignalArchiveNode(SignalArchiveNodeType.CATEGORY, "News", 3, listOf("first.json", "second.json")), 1,
        )
    }
}
