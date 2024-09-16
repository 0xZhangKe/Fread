package com.zhangke.framework.feeds.fetcher

import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class StatusPagingSourceTest {

    @Test
    fun normalCase() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(), pageSize)
        Assert.assertEquals(0, pagingSource.currentPage.size)
        val result = pagingSource.loadNextPage(null)
        val list = result.getOrThrow()
        Assert.assertEquals(pageSize, pagingSource.currentPage.size)
        Assert.assertEquals(pageSize, list.size)
        Assert.assertEquals("0", list.first().dataId)
        Assert.assertEquals("9", list.last().dataId)
    }

    @Test
    fun `should load first page when repeat input first page startId`() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(), pageSize)
        pagingSource.loadNextPage(null)
        val list = pagingSource.loadNextPage("0").getOrThrow()
        Assert.assertEquals(20, pagingSource.currentPage.size)
        Assert.assertEquals(pageSize, list.size)
        Assert.assertEquals("1", list.first().dataId)
        Assert.assertEquals("10", list.last().dataId)
    }

    @Test
    fun nextPageCase() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(), pageSize)
        Assert.assertEquals(0, pagingSource.currentPage.size)
        pagingSource.loadNextPage(null)
        Assert.assertEquals(10, pagingSource.currentPage.size)
        val result = pagingSource.loadNextPage("5")
        Assert.assertEquals(15, pagingSource.currentPage.size)
        val list = result.getOrThrow()
        Assert.assertEquals(pageSize, list.size)
        Assert.assertEquals("6", list.first().dataId)
        Assert.assertEquals("15", list.last().dataId)
        Assert.assertEquals("5", pagingSource.currentPage.first().dataId)
        Assert.assertEquals("19", pagingSource.currentPage.last().dataId)
    }

    @Test
    fun nextNextNextNextPageCase() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(), pageSize)
        pagingSource.loadNextPage(null)
        pagingSource.loadNextPage("5")
        pagingSource.loadNextPage("15")
        pagingSource.loadNextPage("25")
        pagingSource.loadNextPage("35")
        pagingSource.loadNextPage("45")
        val resultList = pagingSource.loadNextPage("55").getOrThrow()
        val currentPage = pagingSource.currentPage
        Assert.assertEquals(15, currentPage.size)
        Assert.assertEquals("55", currentPage.first().dataId)
        Assert.assertEquals("69", currentPage.last().dataId)
        Assert.assertEquals("56", resultList.first().dataId)
        Assert.assertEquals("65", resultList.last().dataId)
    }

    @Test
    fun edgePageCase() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(), pageSize)
        pagingSource.loadNextPage(null)
        pagingSource.loadNextPage("9")
        pagingSource.loadNextPage("9")
        val result = pagingSource.loadNextPage("9").getOrThrow()
        val currentPage = pagingSource.currentPage
        Assert.assertEquals(11, currentPage.size)
        Assert.assertEquals("9", currentPage.first().dataId)
        Assert.assertEquals("19", currentPage.last().dataId)
        Assert.assertEquals("10", result.first().dataId)
        Assert.assertEquals("19", result.last().dataId)
    }

    @Test
    fun nextPageInEdgeCase() = runBlocking {
        val pageSize = 10
        val pagingSource = StatusPagingSource(MockDataSource(3), pageSize)
        pagingSource.loadNextPage(null)
        pagingSource.loadNextPage("9")
        pagingSource.loadNextPage("19")
        val result = pagingSource.loadNextPage("29").getOrThrow()
        val currentPage = pagingSource.currentPage

        println(currentPage.joinToString(",") { it.dataId })
        Assert.assertEquals(16, currentPage.size)
        Assert.assertEquals("19", currentPage.first().dataId)
        Assert.assertEquals("34", currentPage.last().dataId)
        Assert.assertEquals("30", result.first().dataId)
        Assert.assertEquals("34", result.last().dataId)

        val nextResult = pagingSource.loadNextPage("34").getOrThrow()
        Assert.assertEquals(0, nextResult.size)

        val nextNextResult = pagingSource.loadNextPage("33").getOrThrow()
        Assert.assertEquals(1, nextNextResult.size)
    }
}
