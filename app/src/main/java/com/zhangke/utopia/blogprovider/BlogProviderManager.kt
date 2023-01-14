package com.zhangke.utopia.blogprovider

import java.util.*

object BlogProviderManager {

    private lateinit var statusProviderClientList: List<StatusProviderClient>

    lateinit var providerFactoryList: List<StatusProviderFactory>
        private set

    lateinit var sourceInterpreterList: List<BlogSourceInterpreter>
        private set

    fun prepare() {
        val statusProviderClients = ServiceLoader.load(StatusProviderClient::class.java).iterator()
        val list = mutableListOf<StatusProviderClient>()
        while (statusProviderClients.hasNext()) {
            list += statusProviderClients.next()
        }
        statusProviderClientList = list
        providerFactoryList = statusProviderClientList.map { it.statusProviderFactory }
        sourceInterpreterList = statusProviderClientList.map { it.sourceInterpreter }
    }
}