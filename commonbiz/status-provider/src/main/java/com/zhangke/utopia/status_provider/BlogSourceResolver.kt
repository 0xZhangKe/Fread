package com.zhangke.utopia.status_provider

interface BlogSourceResolver {

    suspend fun resolve(content: String): BlogSourceGroup?
}