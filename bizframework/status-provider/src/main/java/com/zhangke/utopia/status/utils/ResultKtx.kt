package com.zhangke.utopia.status.utils

fun <T> List<Result<List<T>>>.collect(): Result<List<T>> {
    if (this.isEmpty()) return Result.success(emptyList())
    if (isNotEmpty() && firstOrNull { it.isSuccess } == null) {
        return first()
    }
    return mapNotNull {
        it.getOrNull()
    }.reduce { list1, list2 ->
        mutableListOf<T>().apply {
            addAll(list1)
            addAll(list2)
        }
    }.let { Result.success(it) }
}
