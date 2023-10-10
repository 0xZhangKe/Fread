package com.zhangke.utopia.activitypubapp.adapter

import com.zhangke.activitypub.entry.ActivityPubPollEntity
import com.zhangke.utopia.status.blog.BlogPoll
import javax.inject.Inject

class ActivityPubPollAdapter @Inject constructor() {

    fun adapt(entity: ActivityPubPollEntity): BlogPoll {
        return BlogPoll(
            id = entity.id,
            expiresAt = entity.expiresAt,
            expired = entity.expired,
            multiple = entity.multiple,
            votesCount = entity.votesCount,
            votersCount = entity.votersCount,
            voted = entity.voted,
            options = entity.options.map { it.convertToPoll() },
            ownVotes = entity.ownVotes ?: emptyList(),
        )
    }

    private fun ActivityPubPollEntity.Option.convertToPoll(): BlogPoll.Option {
        return BlogPoll.Option(
            title = title,
            votesCount = votesCount,
        )
    }
}
