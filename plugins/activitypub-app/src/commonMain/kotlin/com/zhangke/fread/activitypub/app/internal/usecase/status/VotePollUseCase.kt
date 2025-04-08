package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class VotePollUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val pollAdapter: ActivityPubPollAdapter,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        blog: Blog,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status> {
        return clientManager.getClient(role)
            .statusRepo
            .votes(
                id = blog.poll!!.id,
                choices = votedOption.map { it.index },
            )
            .map { pollAdapter.adapt(it) }
            .map { poll ->
                Status.NewBlog(blog.copy(poll = poll))
            }
    }
}
