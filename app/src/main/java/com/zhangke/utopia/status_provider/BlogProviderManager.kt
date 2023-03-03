package com.zhangke.utopia.status_provider

import java.util.*

object BlogProviderManager {

    private lateinit var statusProviderClientList: List<StatusProviderClient>

    lateinit var providerFactoryList: List<StatusProviderFactory>
        private set

    lateinit var sourceResolverList: List<BlogSourceResolver>
        private set

    lateinit var authorizerList: List<StatusProviderAuthorizer>
        private set

    fun prepare() {
        val statusProviderClients = ServiceLoader.load(StatusProviderClient::class.java).iterator()
        val list = mutableListOf<StatusProviderClient>()
        while (statusProviderClients.hasNext()) {
            list += statusProviderClients.next()
        }
        statusProviderClientList = list
        providerFactoryList = statusProviderClientList.map { it.statusProviderFactory }
        sourceResolverList = statusProviderClientList.map { it.sourceResolver }
        authorizerList = statusProviderClientList.map { it.authorizer }
    }
}