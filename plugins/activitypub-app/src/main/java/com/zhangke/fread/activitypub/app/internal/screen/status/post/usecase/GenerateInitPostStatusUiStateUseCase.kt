package com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase

import androidx.core.text.HtmlCompat
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenParams
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.status.model.StatusVisibility
import javax.inject.Inject

class GenerateInitPostStatusUiStateUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
) {

    suspend operator fun invoke(
        screenParams: PostStatusScreenParams,
    ): Result<PostStatusUiState> {
        val allLoggedAccount = accountManager.getAllLoggedAccount()
        val defaultAccount = allLoggedAccount.pickDefaultAccount(screenParams)
            ?: return Result.failure(IllegalStateException("Not login!"))
        return when (screenParams) {
            is PostStatusScreenParams.PostStatusParams -> PostStatusUiState.default(
                account = defaultAccount,
                allLoggedAccount = allLoggedAccount,
                initialContent = null,
                visibility = StatusVisibility.PUBLIC,
                replyToAuthorInfo = null,
            )

            is PostStatusScreenParams.ReplyStatusParams -> buildReplyUiState(
                allLoggedAccount = allLoggedAccount,
                defaultAccount = defaultAccount,
                replyParams = screenParams,
            )

            is PostStatusScreenParams.EditStatusParams -> buildEditPostUiState(
                defaultAccount = defaultAccount,
                allLoggedAccount = allLoggedAccount,
                editParams = screenParams,
            )
        }.let { Result.success(it) }
    }

    private fun List<ActivityPubLoggedAccount>.pickDefaultAccount(
        screenParams: PostStatusScreenParams
    ): ActivityPubLoggedAccount? {
        return if (screenParams.accountUri != null) {
            this.firstOrNull { it.uri == screenParams.accountUri } ?: this.firstOrNull()
        } else {
            this.firstOrNull()
        }
    }

    private fun buildReplyUiState(
        defaultAccount: ActivityPubLoggedAccount,
        allLoggedAccount: List<ActivityPubLoggedAccount>,
        replyParams: PostStatusScreenParams.ReplyStatusParams,
    ): PostStatusUiState {
        val replyWebFinger = replyParams.replyToBlogWebFinger
        val initialContent = if (defaultAccount.platform.baseUrl.host == replyWebFinger.host) {
            "@${replyWebFinger.name} "
        } else {
            "$replyWebFinger "
        }
        return PostStatusUiState.default(
            account = defaultAccount,
            allLoggedAccount = allLoggedAccount,
            initialContent = initialContent,
            visibility = replyParams.replyVisibility,
            replyToAuthorInfo = replyParams,
        )
    }

    private fun buildEditPostUiState(
        defaultAccount: ActivityPubLoggedAccount,
        allLoggedAccount: List<ActivityPubLoggedAccount>,
        editParams: PostStatusScreenParams.EditStatusParams,
    ): PostStatusUiState {
        val blog = editParams.blog
        return PostStatusUiState.default(
            account = defaultAccount,
            allLoggedAccount = allLoggedAccount,
            initialContent = HtmlCompat.fromHtml(blog.content, HtmlCompat.FROM_HTML_MODE_LEGACY)
                .toString(),
            visibility = blog.visibility,
            replyToAuthorInfo = null,
            visibilityChangeable = false,
            accountChangeable = false,
        )
    }
}
