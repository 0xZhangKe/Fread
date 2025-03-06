package com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.initLocale
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusMediaAttachmentFile
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenParams
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusUiState
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaType
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.richtext.parser.HtmlParser
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

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
        val replyWebFinger = replyParams.replyingToBlog.author.webFinger
        val initialContent = if (defaultAccount.platform.baseUrl.host == replyWebFinger.host) {
            "@${replyWebFinger.name} "
        } else {
            "$replyWebFinger "
        }
        return PostStatusUiState.default(
            account = defaultAccount,
            allLoggedAccount = allLoggedAccount,
            content = TextFieldValue(initialContent),
            visibility = replyParams.replyingToBlog.visibility,
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
            content = TextFieldValue(HtmlParser.parseToPlainText(blog.content)),
            visibility = blog.visibility,
            sensitive = editParams.blog.sensitive,
            language = editParams.blog.language?.let { initLocale(it) },
            warningContent = TextFieldValue(HtmlParser.parseToPlainText(editParams.blog.spoilerText)),
            replyToAuthorInfo = null,
            visibilityChangeable = false,
            accountChangeable = false,
            attachment = blog.generateAttachment(),
        )
    }

    private fun Blog.generateAttachment(): PostStatusAttachment? {
        if (mediaList.isNotEmpty()) {
            if (mediaList.first().type == BlogMediaType.VIDEO) {
                return PostStatusAttachment.Video(mediaList.first().toAttachmentFile())
            }
            return PostStatusAttachment.Image(
                mediaList.map { it.toAttachmentFile() }
            )
        }
        val poll = poll
        if (poll != null) {
            val duration = poll.expiresAt
                ?.let { DateParser.parseAll(it) }
                ?.toEpochMilliseconds()
                ?.let { it - getCurrentTimeMillis() }
                ?.takeIf { it > 0 }
                ?.milliseconds
            return PostStatusAttachment.Poll(
                optionList = poll.options.map { it.title },
                multiple = poll.multiple,
                duration = duration ?: 1.days,
            )
        }
        return null
    }

    private fun BlogMedia.toAttachmentFile(): PostStatusMediaAttachmentFile.RemoteFile {
        return PostStatusMediaAttachmentFile.RemoteFile(
            id = this.id,
            url = this.previewUrl.ifNullOrEmpty { this.url },
            description = this.description,
        )
    }
}
