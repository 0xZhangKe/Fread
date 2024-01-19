package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ACTIVITY_PUB_PROTOCOL
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstance
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypub.app.internal.model.ServerDetailContract
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import javax.inject.Inject

class ActivityPubInstanceAdapter @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) {

    fun toPlatform(
        instance: ActivityPubInstanceEntity
    ): BlogPlatform {
        val baseUrl = FormalBaseUrl.parse(instance.domain)!!
        val uri = platformUriTransformer.build(baseUrl)
        return BlogPlatform(
            uri = uri.toString(),
            baseUrl = baseUrl,
            name = instance.title,
            description = instance.description,
            protocol = ACTIVITY_PUB_PROTOCOL,
            thumbnail = instance.thumbnail.url,
        )
    }

    fun toPlatform(
        instance: ActivityPubInstance
    ): BlogPlatform {
        val uri = platformUriTransformer.build(instance.baseUrl)
        return BlogPlatform(
            uri = uri.toString(),
            baseUrl = instance.baseUrl,
            name = instance.title,
            description = instance.description,
            protocol = ACTIVITY_PUB_PROTOCOL,
            thumbnail = instance.thumbnail,
        )
    }

    internal fun toInstance(
        entity: ActivityPubInstanceEntity,
    ): ActivityPubInstance {
        return ActivityPubInstance(
            baseUrl = FormalBaseUrl.parse(entity.domain)!!,
            title = entity.title,
            description = entity.description,
            thumbnail = entity.thumbnail.url,
            version = entity.version,
            activeMonth = entity.usage.users.activeMonth,
            languages = entity.languages,
            rules = entity.rules.map(::convertRule),
            contract = convertContract(entity.contact),
        )
    }

    private fun convertRule(entity: ActivityPubInstanceEntity.Rule): ActivityPubInstanceRule {
        return ActivityPubInstanceRule(id = entity.id, text = entity.text)
    }

    private fun convertContract(entity: ActivityPubInstanceEntity.Contact): ServerDetailContract {
        return ServerDetailContract(
            email = entity.email,
            account = accountEntityAdapter.toAuthor(entity.account),
        )
    }
}
