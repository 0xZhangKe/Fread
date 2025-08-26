package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.collections.container
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubContentAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.onboarding.OnboardingComponent
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class AddActivityPubContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val oAuthor: ActivityPubOAuthor,
    private val contentAdapter: ActivityPubContentAdapter,
    private val onboardingComponent: OnboardingComponent,
    @Assisted private val platform: BlogPlatform,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(platform: BlogPlatform): AddActivityPubContentViewModel
    }

    init {
        launchInViewModel {
            val content = contentAdapter.createContent(
                platform = platform,
                maxOrder = contentRepo.getMaxOrder(),
            )
            val allContent = contentRepo.getAllContent().filterIsInstance<ActivityPubContent>()
            if (!allContent.container { it.id == content.id }) {
                contentRepo.insertContent(content)
            }
        }
    }

    fun onLoginClick() {
        launchInViewModel {
            onboardingComponent.onboardingSuccess()
        }
        oAuthor.startOauth(platform.baseUrl)
    }
}
