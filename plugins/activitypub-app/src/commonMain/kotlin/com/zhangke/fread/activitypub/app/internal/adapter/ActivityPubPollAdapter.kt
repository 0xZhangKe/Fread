package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubPollEntity
import com.zhangke.fread.status.blog.BlogPoll

class ActivityPubPollAdapter () {

    fun adapt(entity: ActivityPubPollEntity): BlogPoll {
        return BlogPoll(
            id = entity.id,
            expiresAt = entity.expiresAt,
            expired = entity.expired,
            multiple = entity.multiple,
            votesCount = entity.votesCount,
            votersCount = entity.votersCount,
            voted = entity.voted,
            options = entity.options.mapIndexed { index, item ->
                item.convertToPoll(index)
            },
            ownVotes = entity.ownVotes ?: emptyList(),
        )
    }

    private fun ActivityPubPollEntity.Option.convertToPoll(index: Int): BlogPoll.Option {
        return BlogPoll.Option(
            index = index,
            title = title,
            votesCount = votesCount,
        )
    }
}