package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.IsUserFirstStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val getUserStatus: GetUserStatusUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val isUserFirstStatus: IsUserFirstStatusUseCase,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusContextUseCase: GetStatusContextUseCase,
    private val clientManager: ActivityPubClientManager,
    private val pollAdapter: ActivityPubPollAdapter,
) : IStatusResolver {

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(
                userInsights = userInsights,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        return null
    }

    override suspend fun checkIsFirstStatus(status: Status): Result<Boolean>? {
        return isUserFirstStatus(status)
    }

    override suspend fun interactive(
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(status, interaction).map { entity ->
            val platform = status.platform
            activityPubStatusAdapter.toStatus(entity, platform)
        }
    }

    override suspend fun votePoll(
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return clientManager.getClient(status.platform.baseUrl)
            .statusRepo
            .votes(
                id = status.intrinsicBlog.poll!!.id,
                choices = votedOption.map { it.index },
            )
            .map {
                pollAdapter.adapt(it)
            }
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

    override suspend fun getStatusContext(status: Status): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID
    }
}
