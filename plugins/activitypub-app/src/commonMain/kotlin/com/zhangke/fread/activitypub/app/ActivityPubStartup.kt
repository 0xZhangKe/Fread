package com.zhangke.fread.activitypub.app

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.collections.container
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubContentAdapter
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.common.content.FreadContentRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class ActivityPubStartup @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val contentAdapter: ActivityPubContentAdapter,
) : ModuleStartup {

    override fun onAppCreate() {
        ApplicationScope.launch {
            accountRepo.onNewAccountFlow.collect { account ->
                delay(500)
                val contentExist = contentRepo.getAllContent()
                    .filterIsInstance<ActivityPubContent>()
                    .container { it.baseUrl == account.baseUrl }
                if (!contentExist) {
                    contentAdapter.createContent(
                        platform = account.platform,
                        maxOrder = contentRepo.getMaxOrder(),
                    ).let { contentRepo.insertContent(it) }
                }
            }
        }
    }
}
