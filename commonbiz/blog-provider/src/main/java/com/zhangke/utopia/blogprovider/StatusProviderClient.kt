package com.zhangke.utopia.blogprovider

interface StatusProviderClient {

    val statusProviderFactory: StatusProviderFactory

    val sourceResolver: BlogSourceResolver

    val sourceRestorer: BlogSourceRestorer

    val authorizer: StatusProviderAuthorizer
}