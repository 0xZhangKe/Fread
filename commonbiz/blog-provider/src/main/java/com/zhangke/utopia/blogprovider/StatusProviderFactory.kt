package com.zhangke.utopia.blogprovider

interface StatusProviderFactory {

    fun createProvider(source: BlogSource): StatusProvider?
}