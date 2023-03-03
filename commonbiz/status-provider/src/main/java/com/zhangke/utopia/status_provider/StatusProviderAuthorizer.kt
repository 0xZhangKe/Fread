package com.zhangke.utopia.status_provider

interface StatusProviderAuthorizer {

    fun applicable(source: StatusSource): Boolean

    /**
     * check this source need login.
     * This will perform authorize if necessary.
     */
    suspend fun checkAuthorizer(source: StatusSource): Boolean

    /**
     * Perform authorize
     */
    fun perform()
}