package com.zhangke.fread.activitypub.app.internal.screen.user.mute

import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.screen.user.common.CommonUserListViewModel
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = MutedUserListViewModel.Factory::class)
class MutedUserListViewModel @AssistedInject constructor(
    private val clientManager: ActivityPubClientManager,
    accountEntityAdapter: ActivityPubAccountEntityAdapter,
    userUriTransformer: UserUriTransformer,
    webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
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

        fun create(role: IdentityRole): MutedUserListViewModel
    }

    private var nextMaxId: String? = null

    override suspend fun loadFirstPageUsersFromServer(accountRepo: AccountsRepo): Result<List<ActivityPubAccountEntity>> {
        nextMaxId = null
        return accountRepo.getMutedUserList()
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
        return accountRepo.getMutedUserList(maxId = nextMaxId)
            .map { pagingResult ->
                this@MutedUserListViewModel.nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data
            }
    }

    fun onUnmuteClick(author: BlogAuthor) {
        launchInViewModel {
            val accountRepo = clientManager.getClient(role).accountRepo
            val authorId = getAuthorId(author) ?: return@launchInViewModel
            accountRepo.unmute(authorId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            userList = state.userList.filterNot { it.uri == author.uri }
                        )
                    }
                }.onFailure {
                    _snackMessageFlow.emitTextMessageFromThrowable(it)
                }
        }
    }
}
