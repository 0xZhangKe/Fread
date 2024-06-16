package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubPollRequestEntity
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import javax.inject.Inject

class PostStatusAttachmentAdapter @Inject constructor() {

    fun toPollRequest(poll: PostStatusAttachment.Poll): ActivityPubPollRequestEntity {
        return ActivityPubPollRequestEntity(
            options = poll.optionList,
            multiple = poll.multiple,
            expiresIn = poll.duration.inWholeSeconds,
            hideTotals = false,
        )
    }
}
