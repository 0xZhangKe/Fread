package com.zhangke.framework.utils

fun Throwable.mapForMessage(newMessage: String): Throwable {
    return if (message.isNullOrEmpty()) {
        val typeName = this::class.simpleName
        RuntimeException("$newMessage-$typeName", this)
    } else {
        this
    }
}

fun <T> Result<T>.mapForErrorMessage(newErrorMessage: String): Result<T> {
    if (this.isSuccess) return this
    val exception = this.exceptionOrNull()
    if (exception == null) {
        return Result.failure(RuntimeException(newErrorMessage))
    }
    return Result.failure(exception.mapForMessage(newErrorMessage))
}
