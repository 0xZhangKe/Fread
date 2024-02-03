package com.zhangke.utopia.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubTagEntity
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.status.model.Hashtag
import javax.inject.Inject

class ActivityPubTagAdapter @Inject constructor() {

    fun adapt(entity: ActivityPubTagEntity): Hashtag {
        val pass2DayUses = entity.history
            .take(2)
            .map { it.accounts }
            .reduce { acc, i -> acc + i }
            .toString()

        return Hashtag(
            name = "#${entity.name}",
            url = entity.url,
            description = textOf(R.string.activity_pub_trends_tag_description, pass2DayUses),
            following = entity.following,
            history = convertHistoryList(entity.history),
        )
    }

    private fun convertHistoryList(
        list: List<ActivityPubTagEntity.History>
    ): Hashtag.History {
        val history = list.map { it.uses.toFloat() }
        val min = history.min()
        val max = history.max()
        val height = max - min
        val padding = height * 0.1F
        val bottom = 0F.coerceAtLeast(min - padding)
        val top = max + height
        return Hashtag.History(
            history = history,
            max = top,
            min = bottom,
        )
    }
}
