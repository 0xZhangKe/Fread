package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusContextEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.DescendantStatus
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext

class GetStatusContextUseCase (
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val loggedAccountProvider: LoggedAccountProvider,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        status: Status,
    ): Result<StatusContext> {
        val loggedAccount = loggedAccountProvider.getAccount(locator)
        val statusId = status.id
        return clientManager.getClient(locator)
            .statusRepo
            .getStatusContext(statusId)
            .map {
                convert(
                    parentId = statusId,
                    entity = it,
                    platform = status.platform,
                    locator = locator,
                    loggedAccount = loggedAccount,
                )
            }
    }

    private suspend fun convert(
        parentId: String,
        entity: ActivityPubStatusContextEntity,
        platform: BlogPlatform,
        locator: PlatformLocator,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusContext {
        return StatusContext(
            ancestors = entity.ancestors.toStatusList(
                locator = locator,
                platform = platform,
                loggedAccount = loggedAccount,
            ),
            status = null,
            descendants = convertDescendantsStatus(
                parentId = parentId,
                descendants = entity.descendants,
                platform = platform,
                locator = locator,
                loggedAccount = loggedAccount,
            ),
        )
    }

    private suspend fun convertDescendantsStatus(
        parentId: String,
        descendants: List<ActivityPubStatusEntity>,
        platform: BlogPlatform,
        locator: PlatformLocator,
        loggedAccount: ActivityPubLoggedAccount?,
    ): List<DescendantStatus> {
        if (descendants.isEmpty()) return emptyList()
        val firstLevelDescendants = descendants.filter { it.inReplyToId == parentId }
        return firstLevelDescendants.map { entity ->
            DescendantStatus(
                entity.toUiState(locator, platform, loggedAccount),
                buildDescendantsStatus(entity.id, descendants, platform, locator, loggedAccount)
            )
        }
    }

    private suspend fun buildDescendantsStatus(
        parentId: String,
        descendants: List<ActivityPubStatusEntity>,
        platform: BlogPlatform,
        locator: PlatformLocator,
        loggedAccount: ActivityPubLoggedAccount?,
    ): DescendantStatus? {
        if (descendants.isEmpty()) return null
        val descentEntity = descendants.firstOrNull { it.inReplyToId == parentId } ?: return null
        val descendantDescendant =
            buildDescendantsStatus(descentEntity.id, descendants, platform, locator, loggedAccount)
        return DescendantStatus(
            status = descentEntity.toUiState(locator, platform, loggedAccount),
            descendantStatus = descendantDescendant,
        )
    }

    private suspend fun List<ActivityPubStatusEntity>.toStatusList(
        locator: PlatformLocator,
        platform: BlogPlatform,
        loggedAccount: ActivityPubLoggedAccount?,
    ): List<StatusUiState> {
        return this.map { statusEntity ->
            statusEntity.toUiState(
                platform = platform,
                loggedAccount = loggedAccount,
                locator = locator,
            )
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(
        locator: PlatformLocator,
        platform: BlogPlatform,
        loggedAccount: ActivityPubLoggedAccount?,
    ): StatusUiState {
        return statusAdapter.toStatusUiState(
            entity = this,
            platform = platform,
            loggedAccount = loggedAccount,
            locator = locator,
        )
    }
}