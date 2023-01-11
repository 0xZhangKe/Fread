package com.zhangke.utopia.blogprovider

interface BlogSourceFactory {

    suspend fun tryCreateSource(uri: String): BlogSource?

    suspend fun validate(source: BlogSource): Boolean
}