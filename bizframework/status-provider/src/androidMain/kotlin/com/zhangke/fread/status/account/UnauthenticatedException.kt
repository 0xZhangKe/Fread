package com.zhangke.fread.status.account

class UnauthenticatedException(override val message: String?) : RuntimeException(message)

fun <T> unauthenticatedResult(message: String? = null) = Result.failure<T>(UnauthenticatedException(message))
