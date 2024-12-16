package com.zhangke.fread.activitypub.app.internal.screen.content

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.usecase.UpdateActivityPubUserListUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import me.tatarka.inject.annotations.Inject

class ActivityPubContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val accountManager: ActivityPubAccountManager,
    private val getUserCreatedList: GetUserCreatedListUseCase,
    private val updateActivityPubUserList: UpdateActivityPubUserListUseCase,
) : ContainerViewModel<ActivityPubContentSubViewModel, ActivityPubContentViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubContentSubViewModel {
        return ActivityPubContentSubViewModel(
            contentRepo = contentRepo,
            getUserCreatedList = getUserCreatedList,
            accountManager = accountManager,
            contentId = params.contentId,
            updateActivityPubUserList = updateActivityPubUserList,
        )
    }

    fun getSubViewModel(contentId: String): ActivityPubContentSubViewModel {
        val params = Params(contentId)
        return obtainSubViewModel(params)
    }

    class Params(val contentId: String) : SubViewModelParams() {

        override val key: String
            get() = contentId.toString()
    }
}
