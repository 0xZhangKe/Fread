package com.zhangke.utopia.blogprovider

interface BlogSourceResolver {

    suspend fun resolve(content: String): BlogSourceGroup?
}