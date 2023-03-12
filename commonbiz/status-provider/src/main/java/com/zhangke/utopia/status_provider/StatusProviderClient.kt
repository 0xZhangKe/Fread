package com.zhangke.utopia.status_provider

class StatusProviderClient(
    val statusProviderFactory: StatusProviderFactory,
    val authorizer: StatusProviderAuthorizer
)

interface Provider{

    fun create(source: StatusSource): StatusProvider
}