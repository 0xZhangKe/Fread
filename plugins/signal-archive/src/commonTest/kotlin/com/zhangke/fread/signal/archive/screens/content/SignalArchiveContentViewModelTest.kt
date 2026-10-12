package com.zhangke.fread.signal.archive.screens.content

import com.zhangke.fread.signal.archive.content.SignalArchiveContent
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
class SignalArchiveContentViewModelTest {
    @BeforeTest
    fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }

    @AfterTest
    fun teardown() { Dispatchers.resetMain() }

    @Test
    fun containerOnlyLoadsIndexAndClampsSelectionWhenNodesDisappear() = runTest {
        var count = 0
        val client = HttpClient(MockEngine { request ->
            assertEquals("/data/rendered.json", request.url.encodedPath)
            respond(if (++count == 1) INDEX else """{"children":[]}""")
        })
        try {
            val vm = SignalArchiveContentViewModel(CONTENT, SignalArchiveRepo(client, Json))
            advanceUntilIdle()
            vm.selectTab(1)
            assertEquals(1, vm.uiState.value.selectedTab)
            vm.refresh()
            advanceUntilIdle()
            assertEquals(SignalArchiveContentUiState(index = com.zhangke.fread.signal.archive.model.SignalArchiveIndex(emptyList()), generation = 2), vm.uiState.value)
            assertEquals(2, count)
        } finally { client.close() }
    }

    @Test
    fun failedRefreshKeepsNavigationAndChildSnapshotGeneration() = runTest {
        var count = 0
        val client = HttpClient(MockEngine {
            if (++count == 1) respond(INDEX) else respond("", HttpStatusCode.ServiceUnavailable)
        })
        try {
            val vm = SignalArchiveContentViewModel(CONTENT, SignalArchiveRepo(client, Json))
            advanceUntilIdle()
            val previous = vm.uiState.value
            vm.refresh()
            advanceUntilIdle()
            assertEquals(previous.copy(indexFailed = true), vm.uiState.value)
            assertTrue(vm.uiState.value.index!!.children.isNotEmpty())
        } finally { client.close() }
    }

    companion object {
        private val CONTENT = SignalArchiveContent("test", 1, "Archive", "https://example.com/data/")
        private const val INDEX = """{"children":[
            {"type":"category","title":"First","articleCount":3,"pages":["page.json"]},
            {"type":"ai","title":"Other","articleCount":0,"pages":[]}
        ]}"""
    }
}
