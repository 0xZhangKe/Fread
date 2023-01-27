package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubApplicableUrl
import com.zhangke.utopia.blogprovider.BlogSource
import com.zhangke.utopia.blogprovider.MetaSourceInfo

internal class UserSource(
    val userId: String,
    metaSourceInfo: MetaSourceInfo,
    sourceServer: String,
    protocol: String,
    sourceName: String,
    sourceDescription: String?,
    avatar: String?,
    extra: JsonObject,
) : BlogSource(
    metaSourceInfo = metaSourceInfo,
    sourceServer = sourceServer,
    protocol = protocol,
    sourceName = sourceName,
    sourceDescription = sourceDescription,
    avatar = avatar,
    extra = extra
) {

    companion object {

        fun BlogSourceScope.newInstance(userSourceExtra: UserSourceExtra): UserSource {
            return UserSource(
                userId = userSourceExtra.userId,
                metaSourceInfo = metaSourceInfo,
                sourceServer = sourceServer,
                protocol = protocol,
                sourceName = sourceName,
                sourceDescription = sourceDescription,
                avatar = avatar,
                extra = extra
            )
        }
    }
}

internal class UserSourceExtra(
    val userId: String,
    val type: ActivityPubSourceType
)

internal class UserSourceResolver : ActivityPubSourceInternalResolver {

    override suspend fun resolve(
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): BlogSource? {
        return null
    }
}