package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class VotePollUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val pollAdapter: ActivityPubPollAdapter,
) {

    suspend operator fun invoke(
        status: Status,
        votedOption: List<BlogPoll.Option>,
        baseUrl: FormalBaseUrl,
    ): Result<Status> {
        return clientManager.getClient(baseUrl)
            .statusRepo
            .votes(
                id = status.intrinsicBlog.poll!!.id,
                choices = votedOption.map { it.index },
            )
            .map { pollAdapter.adapt(it) }
            .map { poll ->
                when (status) {
                    is Status.NewBlog -> {
                        status.copy(blog = status.blog.copy(poll = poll))
                    }

                    is Status.Reblog -> {
                        status.copy(reblog = status.reblog.copy(poll = poll))
                    }
                }
            }
    }
}
