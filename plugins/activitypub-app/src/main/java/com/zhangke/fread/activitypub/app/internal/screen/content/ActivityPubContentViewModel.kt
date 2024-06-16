package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: ActivityPubAccountManager,
    private val getUserCreatedList: GetUserCreatedListUseCase,
) : ContainerViewModel<ActivityPubContentSubViewModel, ActivityPubContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubContentSubViewModel {
        return ActivityPubContentSubViewModel(
            contentConfigRepo = contentConfigRepo,
            getUserCreatedList = getUserCreatedList,
            accountManager = accountManager,
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
