package com.zhangke.utopia.status_provider

interface StatusProviderFactory {

    fun createProvider(source: StatusSource): StatusProvider?
}