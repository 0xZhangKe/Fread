package com.zhangke.framework.feeds.fetcher

import com.zhangke.framework.fetcher.FeedsGenerator
import com.zhangke.framework.fetcher.StatusPagingSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FeedsGeneratorTest {

    @Test
    fun normalCase() = runBlocking {
        val generator = FeedsGenerator<MockData>()
        val pagingSource1 = StatusPagingSource(MockDataSource(), 10)
        val pagingSource2 = StatusPagingSource(MockDataSource(), 10)
        val pagingSource3 = StatusPagingSource(MockDataSource(), 10)
        val pagingSource4 = StatusPagingSource(MockDataSource(), 10)
        val params = listOf(
            FeedsGenerator.GenerateParams(
                pagingSource1,
                listOf(
                    MockData("5", 5L),
                    MockData("3", 3L),
                    MockData("1", 1L),
                )
            ),
            FeedsGenerator.GenerateParams(
                pagingSource2,
                listOf(
                    MockData("7", 9L),
                    MockData("4", 4L),
                    MockData("3", 3L),
                )
            ),
            FeedsGenerator.GenerateParams(
                pagingSource3,
                listOf(
                    MockData("9", 9L),
                    MockData("6", 6L),
                    MockData("1", 1L),
                )
            ),
            FeedsGenerator.GenerateParams(
                pagingSource4,
                listOf(
                    MockData("10", 10L),
                    MockData("11", 11L),
                    MockData("12", 12L),
                )
            ),
        )
        val result = generator.generate(params)
        Assert.assertEquals("1", result.pagingToEndId[pagingSource1])
        Assert.assertEquals("4", result.pagingToEndId[pagingSource2])
        Assert.assertEquals("1", result.pagingToEndId[pagingSource3])
        val expectedList = listOf("1", "1", "3", "3", "4","5")
        Assert.assertEquals(expectedList, result.list.map { it.dataId })
    }
}
