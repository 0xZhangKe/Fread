package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.GroupedCustomEmojiCell
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogEmbed
import com.zhangke.fread.status.model.QuoteApprovalPolicy
import com.zhangke.fread.status.model.StatusVisibility
import kotlin.time.Duration

data class PostStatusUiState(
    val account: ActivityPubLoggedAccount,
    val availableAccountList: List<ActivityPubLoggedAccount>,
    val accountChangeable: Boolean,
    val content: TextFieldValue,
    val attachment: PostStatusAttachment?,
    val visibility: StatusVisibility,
    val visibilityChangeable: Boolean,
    val quoteApprovalPolicyChangeable: Boolean,
    val sensitive: Boolean,
    val warningContent: TextFieldValue,
    val replyToBlog: Blog?,
    val quotingBlog: Blog?,
    val unavailableQuote: BlogEmbed.UnavailableQuote?,
    val emojiList: List<GroupedCustomEmojiCell>,
    val language: Locale,
    val rules: PostBlogRules,
    val publishing: Boolean,
    val mentionState: LoadableState<List<ActivityPubAccountEntity>>,
    val quoteApprovalPolicy: QuoteApprovalPolicy,
) {

    val allowedInputCount: Int get() = rules.maxCharacters - content.text.length

    val showAddAccountIcon: Boolean
        get() = accountChangeable && replyToBlog == null

    val isQuotingBlogMode: Boolean get() = quotingBlog != null || unavailableQuote != null

    fun hasInputtedData(): Boolean {
        if (content.text.isNotEmpty()) return true
        if (attachment != null) return true
        if (sensitive && warningContent.text.isNotEmpty()) return true
        return false
    }

    companion object {

        fun default(
            account: ActivityPubLoggedAccount,
            allLoggedAccount: List<ActivityPubLoggedAccount>,
            visibility: StatusVisibility,
            replyingToBlog: Blog? = null,
            quoteBlog: Blog? = null,
            content: TextFieldValue = TextFieldValue(""),
            sensitive: Boolean = false,
            warningContent: TextFieldValue = TextFieldValue(""),
            language: Locale? = null,
            accountChangeable: Boolean = true,
            visibilityChangeable: Boolean = true,
            attachment: PostStatusAttachment? = null,
            quoteApprovalPolicyChangeable: Boolean = true,
            unavailableQuote: BlogEmbed.UnavailableQuote? = null,
        ): PostStatusUiState {
            return PostStatusUiState(
                account = account,
                availableAccountList = allLoggedAccount,
                content = content,
                attachment = attachment,
                visibility = visibility,
                sensitive = sensitive,
                replyToBlog = replyingToBlog,
                quotingBlog = quoteBlog,
                warningContent = warningContent,
                emojiList = emptyList(),
                language = language ?: getDefaultLocale(),
                rules = PostBlogRules.default(),
                accountChangeable = accountChangeable,
                visibilityChangeable = visibilityChangeable,
                publishing = false,
                mentionState = LoadableState.idle(),
                quoteApprovalPolicy = QuoteApprovalPolicy.PUBLIC,
                quoteApprovalPolicyChangeable = quoteApprovalPolicyChangeable,
                unavailableQuote = unavailableQuote,
            )
        }
    }
}

sealed interface PostStatusAttachment {

    data class Image(val imageList: List<PostStatusMediaAttachmentFile>) : PostStatusAttachment

    data class Video(val video: PostStatusMediaAttachmentFile) : PostStatusAttachment

    data class Poll(
        val optionList: List<String>,
        val multiple: Boolean,
        val duration: Duration,
    ) : PostStatusAttachment

    val asImageOrNull: Image? get() = this as? Image

    val asVideoOrNull: Video? get() = this as? Video

    val asPollAttachment: Poll get() = this as Poll

    val asPollAttachmentOrNull: Poll? get() = this as? Poll
}

sealed interface PostStatusMediaAttachmentFile : PublishPostMedia {

    val previewUri: String

    data class LocalFile(
        val file: ContentProviderFile,
        override val alt: String?,
    ) : PostStatusMediaAttachmentFile {

        override val isVideo: Boolean
            get() = file.isVideo

        override val previewUri: String
            get() = file.uri.toString()

        override val uri: String
            get() = file.uri.toString()

    }

    data class RemoteFile(
        val id: String,
        val url: String,
        val originalAlt: String?,
        override val alt: String?,
        override val isVideo: Boolean,
    ) : PostStatusMediaAttachmentFile {

        override val previewUri: String
            get() = url

        override val uri: String
            get() = url
    }
}

data class PostBlogRules(
    val maxCharacters: Int,
    val maxMediaCount: Int,
    val maxPollOptions: Int,
    val altMaxCharacters: Int,
    val supportsQuotePost: Boolean,
) {
    companion object {

        fun default(
            maxCharacters: Int = 1000,
            maxMediaCount: Int = 4,
            maxPollOptions: Int = 4,
            altMaxCharacters: Int = 1500,
            supportsQuotePost: Boolean = false,
        ): PostBlogRules {
            return PostBlogRules(
                maxCharacters = maxCharacters,
                maxMediaCount = maxMediaCount,
                maxPollOptions = maxPollOptions,
                altMaxCharacters = altMaxCharacters,
                supportsQuotePost = supportsQuotePost,
            )
        }
    }
}
