package com.zhangke.utopia.activitypub.app.internal.adapter

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubTagEntity
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.getActivityPubProtocol
import com.zhangke.utopia.status.model.Hashtag
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject

class ActivityPubTagAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun adapt(entity: ActivityPubTagEntity): Hashtag {
        val yesterdayTimeInMillis = getYesterdayTimeInMillis()
        val pass2DayUses = entity.history
            .filter { it.day * 1000 >= yesterdayTimeInMillis }
            .map { it.accounts }
            .reduce { acc, i -> acc + i }
            .toString()

        return Hashtag(
            name = "#${entity.name}",
            url = entity.url,
            description = textOf(R.string.activity_pub_trends_tag_description, pass2DayUses),
            following = entity.following,
            history = convertHistoryList(entity.history),
            protocol = getActivityPubProtocol(context),
        )
    }

    private fun getYesterdayTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1) // 获取昨天的日期
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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
