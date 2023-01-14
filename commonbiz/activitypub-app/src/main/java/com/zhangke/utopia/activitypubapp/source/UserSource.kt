package com.zhangke.utopia.activitypubapp.source

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.blogprovider.BlogSource

internal class UserSource(private val userId: String) : ActivityPubSource {

    override val type: ActivityPubSourceType = ActivityPubSourceType.USER

}

internal class UserSourceInterpreter: ActivityPubInternalSourceInterpreter{

    override suspend fun applicable(url: ActivityPubApplicableUrl, instance: ActivityPubInstance): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createSource(url: ActivityPubApplicableUrl, instance: ActivityPubInstance): BlogSource {
        TODO("Not yet implemented")
    }

    override suspend fun validate(source: BlogSource): Boolean {
        TODO("Not yet implemented")
    }
}