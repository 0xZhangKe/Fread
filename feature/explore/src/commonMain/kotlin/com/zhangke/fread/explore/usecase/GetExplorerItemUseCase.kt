package com.zhangke.fread.explore.usecase

import com.zhangke.fread.explore.model.ExplorerItem
import com.zhangke.fread.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class GetExplorerItemUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    private companion object {

        private const val DEFAULT_LIMIT = 40
    }

    suspend operator fun invoke(
        role: IdentityRole,
        type: ExplorerFeedsTabType,
        offset: Int,
        maxId: String?,
    ): Result<List<ExplorerItem>> {
        val statusResolver = statusProvider.statusResolver
        return when (type) {
            ExplorerFeedsTabType.USERS -> {
                if (offset > 0 || !maxId.isNullOrEmpty()) return Result.success(emptyList())
                statusResolver
                    .getSuggestionAccounts(role)
                    .map { list -> list.map { ExplorerItem.ExplorerUser(it, false) } }
            }

            ExplorerFeedsTabType.HASHTAG -> {
                statusResolver.getHashtag(
                    role = role,
                    limit = DEFAULT_LIMIT,
                    offset = offset,
                ).map { list -> list.map { ExplorerItem.ExplorerHashtag(it) } }
            }

            ExplorerFeedsTabType.STATUS -> {
                statusResolver.getPublicTimeline(
                    role = role,
                    limit = DEFAULT_LIMIT,
                    maxId = maxId,
                ).map { list ->
                    list.map { ExplorerItem.ExplorerStatus(it) }
                }
            }
        }
    }
}
