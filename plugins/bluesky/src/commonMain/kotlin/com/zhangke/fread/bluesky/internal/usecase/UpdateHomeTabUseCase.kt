package com.zhangke.fread.bluesky.internal.usecase

import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UpdateHomeTabUseCase @Inject constructor(
    private val getFeeds: GetFollowingFeedsUseCase,
    private val contentRepo: FreadContentRepo,
) {

    suspend operator fun invoke(
        contentId: String,
        role: IdentityRole,
    ) {
        val content = contentRepo.getContent(contentId)
            ?.takeIf { it is BlueskyContent }
            ?.let { it as BlueskyContent } ?: return
        getFeeds(role).onSuccess { feeds ->
            contentRepo.insertContent(content.copy(feedsList = feeds))
        }
    }
}
