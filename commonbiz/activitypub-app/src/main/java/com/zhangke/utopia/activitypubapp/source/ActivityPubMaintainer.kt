package com.zhangke.utopia.activitypubapp.source

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.activitypubapp.utils.ActivityPubUrl
import com.zhangke.utopia.status_provider.StatusSource
import com.zhangke.utopia.status_provider.StatusSourceMaintainer

internal class ActivityPubMaintainer(
    override val name: String,
    override val description: String,
    override val thumbnail: String?,
    val activityPubUrl: ActivityPubUrl,
    override val sourceList: List<StatusSource>
) : StatusSourceMaintainer {

    override val url: String = activityPubUrl.completenessUrl

    companion object {

        fun fromActivityPubInstance(
            instance: ActivityPubInstance,
            sourceList: List<StatusSource>
        ): ActivityPubMaintainer {
            return ActivityPubMaintainer(
                name = instance.title,
                description = instance.description,
                thumbnail = instance.thumbnail.url,
                activityPubUrl = ActivityPubUrl.create(instance.domain)!!,
                sourceList = sourceList
            )
        }
    }
}