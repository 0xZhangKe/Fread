package com.zhangke.framework.utils

fun <T> List<Result<List<T>>>.collect(): Result<List<T>> {
    if (isNotEmpty() && any { it.isSuccess }.not()) {
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

fun <T> Result<T>.exceptionOrThrow(): Throwable {
    return exceptionOrNull() ?: throw IllegalStateException("Result is success!")
}

fun Result<*>.ignoreContent(): Result<Unit> = map {}
