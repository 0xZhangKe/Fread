package com.zhangke.fread.activitypub.app.internal.screen.user.follow

import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserListViewModel
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = FollowViewModel.Factory::class)
class FollowViewModel @AssistedInject constructor(
    private val userUriTransformer: UserUriTransformer,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    @Assisted private val role: IdentityRole,
    @Assisted private val userUri: FormalUri,
    @Assisted private val isFollowing: Boolean,
) : CommonUserListViewModel(
    role = role,
    clientManager = clientManager,
    accountEntityAdapter = accountEntityAdapter,
    userUriTransformer = userUriTransformer,
    webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
) {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(role: IdentityRole, userUri: FormalUri, isFollowing: Boolean): FollowViewModel
    }

    private var userId: String? = null

    private var nextMaxId: String? = null

    init {
        initData()
    }

    override suspend fun loadFirstPageUsersFromServer(accountRepo: AccountsRepo): Result<List<ActivityPubAccountEntity>> {
        nextMaxId = null
        val userId = userId ?: getUserIdByUri(userUri)?.also {
            this.userId = it
        }
        if (userId == null) {
            return Result.failure(IllegalStateException("Invalid user uri: $userUri"))
        }
        return loadFollowList(id = userId, accountRepo = accountRepo)
    }

    override suspend fun loadNextPageUsersFromServer(accountRepo: AccountsRepo): Result<List<ActivityPubAccountEntity>> {
        val nextMaxId = nextMaxId
        if (nextMaxId.isNullOrEmpty()) {
            return Result.failure(IllegalStateException("nextMaxId is null or empty"))
        }
        val userId = userId ?: getUserIdByUri(userUri)?.also {
            this.userId = it
        }
        if (userId == null) {
            return Result.failure(IllegalStateException("Invalid user uri: $userUri"))
        }
        return loadFollowList(id = userId, accountRepo = accountRepo, maxId = nextMaxId)
    }

    override fun onLoadMore() {
        if (nextMaxId.isNullOrEmpty().not()) {
            super.onLoadMore()
        }
    }

    private suspend fun loadFollowList(
        id: String,
        accountRepo: AccountsRepo,
        maxId: String? = null,
    ): Result<List<ActivityPubAccountEntity>> {
        return if (isFollowing) {
            accountRepo.getFollowing(
                id = id,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            ).map { result ->
                nextMaxId = result.pagingInfo.nextMaxId
                result.data
            }
        } else {
            accountRepo.getFollowers(
                id = id,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            ).map { result ->
                nextMaxId = result.pagingInfo.nextMaxId
                result.data
            }
        }
    }
}
