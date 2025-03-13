package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubContentAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class AddActivityPubContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val accountManager: ActivityPubAccountManager,
    private val oAuthor: ActivityPubOAuthor,
    private val contentAdapter: ActivityPubContentAdapter,
    @Assisted private val platform: BlogPlatform,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(platform: BlogPlatform): AddActivityPubContentViewModel
    }

    private val _uiState = MutableStateFlow(LoadableState.loading<AddActivityPubContentUiState>())
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            val allContent = contentRepo.getAllContent().filterIsInstance<ActivityPubContent>()
            if (allContent.container { it.baseUrl == platform.baseUrl }) {
                _uiState.updateToSuccess(
                    AddActivityPubContentUiState(
                        contentExist = true,
                        account = accountManager.getAccount(platform.baseUrl),
                    )
                )
            } else {
                val content = contentAdapter.createContent(
                    platform = platform,
                    maxOrder = contentRepo.getMaxOrder(),
                )
                contentRepo.insertContent(content)
                _uiState.updateToSuccess(
                    AddActivityPubContentUiState(
                        contentExist = false,
                        account = accountManager.getAccount(platform.baseUrl),
                    )
                )
            }
        }
    }

    fun onLoginClick() {
        oAuthor.startOauth(platform.baseUrl)
    }
}
