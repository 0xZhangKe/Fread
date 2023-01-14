package com.zhangke.utopia.blogprovider

interface StatusProviderClient {

    val statusProviderFactory: StatusProviderFactory

    val sourceInterpreter: BlogSourceInterpreter
}