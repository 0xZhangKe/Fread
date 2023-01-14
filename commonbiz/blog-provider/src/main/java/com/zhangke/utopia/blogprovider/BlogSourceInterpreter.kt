package com.zhangke.utopia.blogprovider

/**
 * Try create BlogSource by input uri.
 */
interface BlogSourceInterpreter {

    /**
     * Can this interpreter applicable this uri.
     */
    suspend fun applicable(uri: String): Boolean

    suspend fun createSourceGroup(uri: String): BlogSourceGroup

    suspend fun validate(source: BlogSource): Boolean
}