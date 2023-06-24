package com.zhangke.framework.feeds.fetcher

import javax.inject.Inject

class FeedsGenerator<Value : StatusData> @Inject constructor() {

    fun generate(
        paramsList: List<GenerateParams<Value>>,
    ): GenerateResult<Value> {
        val minDatetime = paramsList.mapNotNull {
            it.statusList.takeIf { list -> list.isNotEmpty() }
        }.minOf { it.last().datetime }
        val resultList = mutableListOf<Value>()
        val pagingToEndId = HashMap<StatusPagingSource<*, *>, String>()
        paramsList.forEach { param ->
            val statusList = param.statusList
            val lastOne = statusList.lastOrNull { it.datetime <= minDatetime } ?: return@forEach
            val lastIndex = statusList.indexOf(lastOne)
            resultList += statusList.subList(0, lastIndex + 1)
            pagingToEndId[param.pagingSource] = lastOne.dataId
        }
        return GenerateResult(
            list = resultList.sortedBy { it.datetime },
            pagingToEndId = pagingToEndId,
        )
    }

    data class GenerateParams<Value : StatusData>(
        val pagingSource: StatusPagingSource<*, *>,
        val statusList: List<Value>,
    )

    data class GenerateResult<Value : StatusData>(
        val list: List<Value>,
        val pagingToEndId: Map<StatusPagingSource<*, *>, String>,
    )
}
