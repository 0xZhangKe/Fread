package com.zhangke.fread.status.account

class AuthenticationFailureException(override val message: String?) : RuntimeException(message)

val Throwable.isAuthenticationFailure: Boolean
    get() = this is AuthenticationFailureException
