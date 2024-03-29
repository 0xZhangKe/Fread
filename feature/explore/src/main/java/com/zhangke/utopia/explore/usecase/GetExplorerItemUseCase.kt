package com.zhangke.utopia.explore.usecase

import com.zhangke.utopia.explore.model.ExplorerItem
import com.zhangke.utopia.explore.screens.home.tab.ExplorerFeedsTabType
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import javax.inject.Inject

class GetExplorerItemUseCase @Inject constructor(
    private val statusProvider: StatusProvider,
) {

    suspend operator fun invoke(
        account: LoggedAccount,
        type: ExplorerFeedsTabType,
    ): Result<List<ExplorerItem>> {
        val statusResolver = statusProvider.statusResolver
        return when (type) {
            ExplorerFeedsTabType.USERS -> {
                statusResolver
                    .getSuggestionAccounts(account.uri)
                    .map { list -> list.map { ExplorerItem.ExplorerUser(it) } }
            }
            ExplorerFeedsTabType.HASHTAG -> {
                statusResolver.getHashtag(account.uri)
                    .map { list -> list.map { ExplorerItem.ExplorerHashtag(it) } }
            }
        }
    }
}
