package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubTagEntity
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.model.ActivityPubTag
import com.zhangke.utopia.activitypubapp.model.ActivityPubTagHistory
import javax.inject.Inject

class ActivityPubTagAdapter @Inject constructor() {

    fun adapt(entity: ActivityPubTagEntity): ActivityPubTag {
        val pass2DayUses = entity.history
            .take(2)
            .map { it.accounts }
            .reduce { acc, i -> acc + i }
            .toString()

        return ActivityPubTag(
            name = "#${entity.name}",
            url = entity.url,
            description = textOf(R.string.activity_pub_trends_tag_description, pass2DayUses),
            following = entity.following,
            history = convertHistoryList(entity.history),
        )
    }

    private fun convertHistoryList(
        list: List<ActivityPubTagEntity.History>
    ): ActivityPubTagHistory {
        val history = list.map { it.uses.toFloat() }
        val min = history.min()
        val max = history.max()
        val height = max - min
        val padding = height * 0.1F
        val bottom = 0F.coerceAtLeast(min - padding)
        val top = max + height
        return ActivityPubTagHistory(
            history = history,
            max = top,
            min = bottom,
        )
    }
}
