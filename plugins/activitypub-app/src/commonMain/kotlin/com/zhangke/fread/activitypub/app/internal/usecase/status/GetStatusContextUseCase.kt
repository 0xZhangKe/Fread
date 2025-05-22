package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusContextEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.DescendantStatus
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
        val statusId = status.id
        return clientManager.getClient(role)
            .statusRepo
            .getStatusContext(statusId)
            .map {
                convert(
                    parentId = statusId,
                    entity = it,
                    platform = status.platform,
                    role = role,
                    loggedAccount = loggedAccount,
                )
            }
    }

    private suspend fun convert(
        parentId: String,
        entity: ActivityPubStatusContextEntity,
        platform: BlogPlatform,
        role: IdentityRole,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusContext {
        return StatusContext(
            ancestors = entity.ancestors.toStatusList(
                role = role,
                platform = platform,
                loggedAccount = loggedAccount,
            ),
            status = null,
            descendants = convertDescendantsStatus(
                parentId = parentId,
                descendants = entity.descendants,
                platform = platform,
                role = role,
                loggedAccount = loggedAccount,
            ),
        )
    }

    private suspend fun convertDescendantsStatus(
        parentId: String,
        descendants: List<ActivityPubStatusEntity>,
        platform: BlogPlatform,
        role: IdentityRole,
        loggedAccount: ActivityPubLoggedAccount?,
    ): List<DescendantStatus> {
        if (descendants.isEmpty()) return emptyList()
        val firstLevelDescendants = descendants.filter { it.inReplyToId == parentId }
        return firstLevelDescendants.map { entity ->
            DescendantStatus(
                entity.toUiState(role, platform, loggedAccount),
                buildDescendantsStatus(entity.id, descendants, platform, role, loggedAccount)
            )
        }
    }

    private suspend fun buildDescendantsStatus(
        parentId: String,
        descendants: List<ActivityPubStatusEntity>,
        platform: BlogPlatform,
        role: IdentityRole,
        loggedAccount: ActivityPubLoggedAccount?,
    ): DescendantStatus? {
        if (descendants.isEmpty()) return null
        val descentEntity = descendants.firstOrNull { it.inReplyToId == parentId } ?: return null
        val descendantDescendant =
            buildDescendantsStatus(descentEntity.id, descendants, platform, role, loggedAccount)
        return DescendantStatus(
            status = descentEntity.toUiState(role, platform, loggedAccount),
            descendantStatus = descendantDescendant,
        )
    }

    private suspend fun List<ActivityPubStatusEntity>.toStatusList(
        role: IdentityRole,
        platform: BlogPlatform,
        loggedAccount: ActivityPubLoggedAccount?,
    ): List<StatusUiState> {
        return this.map { statusEntity ->
            statusEntity.toUiState(
                platform = platform,
                loggedAccount = loggedAccount,
                role = role,
            )
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(
        role: IdentityRole,
        platform: BlogPlatform,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusUiState {
        return statusAdapter.toStatusUiState(
            entity = this,
            platform = platform,
            loggedAccount = loggedAccount,
            role = role,
        )
    }
}
