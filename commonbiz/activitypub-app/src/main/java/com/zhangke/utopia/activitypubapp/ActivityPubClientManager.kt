package com.zhangke.utopia.activitypubapp

import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.framework.architect.http.newRetrofit
import com.zhangke.framework.architect.json.globalGson

/**
 * Created by ZhangKe on 2022/12/4.
 */
object ActivityPubClientManager {

    private const val MASTDON = "https://mastodon.social"

    private const val CMX_IM = "https://m.cmx.im"

    private val clients = mutableMapOf<String, ActivityPubClient>()

    fun getMastodonClient(): ActivityPubClient {
        return clients.getOrPut(MASTDON) {
            buildActivityPubClient(mastodonUtopiaApplication, MASTDON)
        }
    }

    fun getCmxImClient(): ActivityPubClient {
        return clients.getOrPut(MASTDON) {
            buildActivityPubClient(cmimUtopiaApplication, CMX_IM)
        }
    }

    private fun buildActivityPubClient(
        app: ActivityPubApplication,
        domain: String,
    ): ActivityPubClient {
        return ActivityPubClient(
            application = app,
            baseUrl = MASTDON,
            newRetrofit(domain),
            gson = globalGson,
            onAuthorizeFailed = {

            }
        )
    }
}