package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.repo.account.AccountListsRepo
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: ActivityPubAccountManager,
    private val accountListsRepo: AccountListsRepo,
) : ContainerViewModel<ActivityPubContentSubViewModel, ActivityPubContentViewModel.Params>() {


    override fun createSubViewModel(params: Params): ActivityPubContentSubViewModel {
        return ActivityPubContentSubViewModel(
            contentConfigRepo = contentConfigRepo,
            accountManager = accountManager,
            accountListsRepo = accountListsRepo,
            configId = params.configId,
        )
    }

    fun getSubViewModel(configId: Long): ActivityPubContentSubViewModel {
        val params = Params(configId)
        return obtainSubViewModel(params)
    }

    class Params(val configId: Long) : SubViewModelParams() {

        override val key: String
            get() = configId.toString()
    }
}
