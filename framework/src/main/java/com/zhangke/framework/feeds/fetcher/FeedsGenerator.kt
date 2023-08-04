package com.zhangke.framework.feeds.fetcher

import javax.inject.Inject

class FeedsGenerator<Value> @Inject constructor() {

    fun generate(
        paramsList: List<GenerateParams<Value>>,
    ): GenerateResult<Value> {
        val maxLastDatetime = paramsList.minOf { params ->
            params.pagingSource.getDatetime(params.statusList.last())
        }
        val resultList = mutableListOf<Pair<StatusPagingSource<*, Value>, Value>>()
        val pagingToEndId = HashMap<StatusPagingSource<*, *>, String>()
        paramsList.forEach { param ->
            val statusList = param.statusList
            val pagingSource = param.pagingSource
            statusList.sortedBy { pagingSource.getDatetime(it) }
            val lastOne = statusList.lastOrNull { pagingSource.getDatetime(it) >= maxLastDatetime } ?: return@forEach
            val lastIndex = statusList.indexOf(lastOne)
            resultList += statusList.subList(0, lastIndex + 1).map {
                pagingSource to it
            }
            pagingToEndId[param.pagingSource] = pagingSource.getDataId(lastOne)
        }
        return GenerateResult(
            list = resultList.sortedByDescending { it.first.getDatetime(it.second) }.map { it.second },
            pagingToEndId = pagingToEndId,
        )
    }

    data class GenerateParams<Value>(
        val pagingSource: StatusPagingSource<*, Value>,
        val statusList: List<Value>,
    )

    data class GenerateResult<Value>(
        val list: List<Value>,
        val pagingToEndId: Map<StatusPagingSource<*, *>, String>,
    )
}
