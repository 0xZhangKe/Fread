package com.zhangke.fread.activitypub.app.internal.migrate

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.common.content.FreadContentRepo
import me.tatarka.inject.annotations.Inject

class ActivityPubContentMigrator @Inject constructor(
    private val freadContentRepo: FreadContentRepo,
    private val accountRepo: ActivityPubLoggedAccountRepo,
) {

    suspend fun migrate() {
        val allContent =
            freadContentRepo.getAllOldContents().filter { it.second is ActivityPubContent }
        if (allContent.isEmpty()) return
        val allAccounts = accountRepo.queryAll()
        allContent.map { it.second as ActivityPubContent }
            .map { content ->
                if (content.accountUri != null) {
                    content
                } else {
                    val account = allAccounts.firstOrNull { it.baseUrl == content.baseUrl }
                    content.copy(accountUri = account?.uri)
                }
            }.let { freadContentRepo.insertAll(it) }
        for (content in allContent) {
            freadContentRepo.deleteOldContents(content.first)
        }
    }
}
