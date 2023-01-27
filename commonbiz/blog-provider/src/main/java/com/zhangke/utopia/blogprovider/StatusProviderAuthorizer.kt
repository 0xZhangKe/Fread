package com.zhangke.utopia.blogprovider

interface StatusProviderAuthorizer {

    fun applicable(source: BlogSource): Boolean

    /**
     * check this source need login.
     * This will perform authorize if necessary.
     */
    suspend fun checkAuthorizer(source: BlogSource): Boolean

    /**
     * Perform authorize
     */
    fun perform()
}