package com.zhangke.utopia.activitypubapp.source

import com.google.gson.JsonObject
import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypubapp.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypubapp.obtainActivityPubClient
import com.zhangke.utopia.activitypubapp.source.UserSource.Companion.newInstance
import com.zhangke.utopia.activitypubapp.utils.WebFingerUtil
import com.zhangke.utopia.status_provider.*

internal class UserSource(
    val userId: String,
    metaSourceInfo: MetaSourceInfo,
    acct: String,
    protocol: String,
    sourceName: String,
    sourceDescription: String?,
    avatar: String?,
) : StatusSource {

    override val uri: String
        get() = TODO("Not yet implemented")

    override val provider: StatusProvider
        get() = TODO("Not yet implemented")

    override suspend fun requestAdd(addFunction: suspend (valid: Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    companion object {

        fun BlogSourceScope.newInstance(userSourceExtra: UserSourceExtra): UserSource {
            return UserSource(
                userId = userSourceExtra.userId,
                metaSourceInfo = metaSourceInfo,
                acct = uri,
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

/**
 * Supported:
 * - @jw@jakewharton.com
 * - acct:@jw@jakewharton.com
 * - https://m.cmx.im/@jw@jakewharton.com
 * - https://m.cmx.im/@AtomZ
 * - m.cmx.im/@jw@jakewharton.com
 * - jakewharton.com/@jw
 */
internal class UserSourceResolver : BlogSourceResolver {

    override suspend fun resolve(content: String): BlogSourceGroup? {
        val webFinger = WebFingerUtil.findWebFinger(content) ?: return null
        val client = obtainActivityPubClient(webFinger.host!!)
        val user = client.accountRepo
            .lookup(webFinger.value!!)
            .getOrNull()
            ?: return null
        return BlogSourceGroup(
            metaSourceInfo = user.toMetaSourceInfo(),
            listOf(
                user.toBlogSource(webFinger.value!!, ActivityPubSourceType.USER_STATUS),
                user.toBlogSource(
                    webFinger.value!!,
                    ActivityPubSourceType.USER_STATUS_EXCLUDE_REPLIES
                ),
            )
        )
    }

    private fun ActivityPubAccount.toBlogSource(
        webFinger: String,
        type: ActivityPubSourceType,
    ): StatusSource {
        val extra = UserSourceExtra(userId = id, type = type)
        val scope = BlogSourceScope(
            metaSourceInfo = toMetaSourceInfo(),
            uri = webFinger,
            sourceDescription = note,
            sourceName = displayName,
            avatar = avatar,
            protocol = ACTIVITY_PUB_PROTOCOL,
            extra = globalGson.toJsonTree(extra).asJsonObject
        )
        return scope.newInstance(extra)
    }

    private fun ActivityPubAccount.toMetaSourceInfo(): MetaSourceInfo {
        return MetaSourceInfo(
            url = url,
            name = displayName,
            thumbnail = avatar,
            description = note,
            extra = null,
        )
    }
}