package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
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

        fun newInstance(
            extra: JsonObject,
            userSourceExtra: UserSourceExtra,
            metaSourceInfo: MetaSourceInfo,
            sourceServer: String,
            protocol: String,
            sourceName: String,
            sourceDescription: String?,
            avatar: String?,
        ): UserSource {
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

internal class UserSourceInterpreter : ActivityPubInternalSourceInterpreter {

    override suspend fun applicable(
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createSource(
        url: ActivityPubApplicableUrl,
        instance: ActivityPubInstance
    ): BlogSource {
        TODO("Not yet implemented")
    }

    override suspend fun validate(source: BlogSource): Boolean {
        TODO("Not yet implemented")
    }
}