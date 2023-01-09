package com.zhangke.utopia.activitypubapp.servers

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.newActivityPubApplication
import com.zhangke.utopia.activitypubapp.newActivityPubClient
import com.zhangke.utopia.blogprovider.BlogServer

object ActivityPubServers {

    fun getRecommendServers(): List<BlogServer> {
        return emptyList()
    }

    suspend fun getServerInstance(domain: String): Result<ActivityPubInstance> {
        val app = newActivityPubApplication(domain)
        val client = newActivityPubClient(app)
        return client.instanceRepo.getInstanceInformation()
    }
}