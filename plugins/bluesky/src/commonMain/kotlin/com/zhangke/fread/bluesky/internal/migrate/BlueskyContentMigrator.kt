package com.zhangke.fread.bluesky.internal.migrate

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.common.content.FreadContentRepo

class BlueskyContentMigrator(
    private val freadContentRepo: FreadContentRepo,
    private val accountManager: BlueskyLoggedAccountManager,
) {

    suspend fun migrate() {
        val allContent = freadContentRepo.getAllOldContents().filter { it.second is BlueskyContent }
        if (allContent.isEmpty()) return
        val allAccounts = accountManager.getAllAccount()
        allContent.map { it.second as BlueskyContent }
            .map { content ->
                if (content.accountUri != null) {
                    content
                } else {
                    val account = allAccounts.firstOrNull { it.platform.baseUrl == content.baseUrl }
                    content.copy(accountUri = account?.uri)
                }
            }.let { freadContentRepo.insertAll(it) }
        for (content in allContent) {
            freadContentRepo.deleteOldContents(content.first)
        }
    }
}
