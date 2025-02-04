package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusContextEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import me.tatarka.inject.annotations.Inject

class GetStatusContextUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
    ): Result<StatusContext> {
        val loggedAccount = loggedAccountProvider.getAccount(role)
        val isOwner =
            loggedAccount?.webFinger?.equalsDomain(status.intrinsicBlog.author.webFinger) == true
        return clientManager.getClient(role)
            .statusRepo
            .getStatusContext(status.id)
            .map {
                convert(
                    entity = it,
                    platform = status.platform,
                    role = role,
                    logged = loggedAccount != null,
                    isOwner = isOwner,
                )
            }
    }

    private suspend fun convert(
        entity: ActivityPubStatusContextEntity,
        platform: BlogPlatform,
        role: IdentityRole,
        logged: Boolean,
        isOwner: Boolean,
    ): StatusContext {
        return StatusContext(
            ancestors = entity.ancestors.toStatusList(
                role = role,
                platform = platform,
                logged = logged,
                isOwner = isOwner,
            ),
            status = null,
            descendants = entity.descendants.toStatusList(
                role = role,
                platform = platform,
                logged = logged,
                isOwner = isOwner,
            ),
        )
    }

    private suspend fun List<ActivityPubStatusEntity>.toStatusList(
        role: IdentityRole,
        platform: BlogPlatform,
        logged: Boolean,
        isOwner: Boolean,
    ): List<StatusUiState> {
        return this.map { statusEntity ->
            statusAdapter.toStatusUiState(
                entity = statusEntity,
                platform = platform,
                logged = logged,
                isOwner = isOwner,
                role = role,
            )
        }
    }
}
