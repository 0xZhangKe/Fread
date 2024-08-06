package com.zhangke.fread.activitypub.app.internal.screen.user.block

import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserListViewModel
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = BlockedUserListViewModel.Factory::class)
class BlockedUserListViewModel @AssistedInject constructor(
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    @Assisted private val role: IdentityRole,
) : CommonUserListViewModel(
    role = role,
    clientManager = clientManager,
    accountEntityAdapter = accountEntityAdapter,
    userUriTransformer = userUriTransformer,
    webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
) {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(role: IdentityRole): BlockedUserListViewModel
    }

    private var nextMaxId: String? = null

    override suspend fun loadFirstPageUsersFromServer(accountRepo: AccountsRepo): Result<List<ActivityPubAccountEntity>> {
        nextMaxId = null
        return accountRepo.getBlockedUserList()
            .map { pagingResult ->
                nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data
            }
    }

    override fun onLoadMore() {
        if (nextMaxId.isNullOrEmpty().not()) {
            super.onLoadMore()
        }
    }

    override suspend fun loadNextPageUsersFromServer(accountRepo: AccountsRepo): Result<List<ActivityPubAccountEntity>> {
        val nextMaxId = nextMaxId
        if (nextMaxId.isNullOrEmpty()) {
            return Result.failure(IllegalStateException("nextMaxId is null or empty"))
        }
        return accountRepo.getBlockedUserList(maxId = nextMaxId)
            .map { pagingResult ->
                this@BlockedUserListViewModel.nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data
            }
    }
}
