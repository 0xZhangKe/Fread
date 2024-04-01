package com.zhangke.utopia.explore.usecase

import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class GetExplorerItemUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) {

    private companion object {

        private const val DEFAULT_LIMIT = 40
    }

    suspend operator fun invoke(
        accountUri: FormalUri,
        type: ExplorerFeedsTabType,
        offset: Int,
        sinceId: String,
    ): Result<List<ExplorerItem>> {
        val statusResolver = statusProvider.statusResolver
        return when (type) {
            ExplorerFeedsTabType.USERS -> {
                if (offset > 0 || sinceId.isNotEmpty()) return Result.success(emptyList())
                statusResolver
                    .getSuggestionAccounts(accountUri)
                    .map { list -> list.map { ExplorerItem.ExplorerUser(it, false) } }
            }
            ExplorerFeedsTabType.HASHTAG -> {
                statusResolver.getHashtag(
                    userUri = accountUri,
                    limit = DEFAULT_LIMIT,
                    offset = offset,
                ).map { list -> list.map { ExplorerItem.ExplorerHashtag(it) } }
            }
            ExplorerFeedsTabType.STATUS -> {
                statusResolver.getPublicTimeline(
                    userUri = accountUri,
                    limit = DEFAULT_LIMIT,
                    sinceId = sinceId,
                ).map { list -> list.map { ExplorerItem.ExplorerStatus(buildStatusUiState(it)) } }
            }
        }
    }
}
