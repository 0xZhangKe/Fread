package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubTagEntity
import com.zhangke.framework.composable.textOf
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.common.utils.getCurrentInstant
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.Hashtag
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject

class ActivityPubTagAdapter @Inject constructor() {

    suspend fun adapt(entity: ActivityPubTagEntity): Hashtag {
        val yesterdayTimeInMillis = getYesterdayTimeInMillis()
        val pass2DayUses = entity.history
            .filter { it.day * 1000 >= yesterdayTimeInMillis }
            .map { it.accounts }
            .takeIf { it.isNotEmpty() }
            ?.reduce { acc, i -> acc + i }
            .toString()

        return Hashtag(
            name = "#${entity.name}",
            url = entity.url,
            description = textOf(LocalizedString.activity_pub_trends_tag_description, pass2DayUses),
            following = entity.following,
            history = convertHistoryList(entity.history),
            protocol = createActivityPubProtocol(),
        )
    }

    private fun getYesterdayTimeInMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = getCurrentInstant().toLocalDateTime(timeZone)
        // 获取昨天的日期
        return LocalDateTime(today.year, today.month, today.dayOfMonth, 0, 0, 0)
            .toInstant(timeZone)
            .toEpochMilliseconds()
    }

    private fun convertHistoryList(
        list: List<ActivityPubTagEntity.History>
    ): Hashtag.History {
        if (list.isEmpty()) {
            return Hashtag.History(
                history = emptyList(),
                max = 0F,
                min = 0F,
            )
        }
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
