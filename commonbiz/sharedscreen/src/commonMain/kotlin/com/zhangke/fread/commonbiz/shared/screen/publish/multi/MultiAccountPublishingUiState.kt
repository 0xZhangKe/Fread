package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.fread.commonbiz.shared.model.PostInteractionSetting
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_follower_only
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_mentioned_only
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_public
import com.zhangke.fread.commonbiz.shared.screen.post_status_scope_unlisted
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.model.isActivityPub
import com.zhangke.fread.status.model.isBluesky
import org.jetbrains.compose.resources.StringResource

data class MultiAccountPublishingUiState(
    val addedAccounts: List<MultiPublishingAccountUiState>,
    val allAccounts: List<MultiPublishingAccountWithRules>,
    val publishing: Boolean,
    val content: TextFieldValue,
    val globalRules: PublishBlogRules,
    val medias: List<PublishPostMediaAttachmentFile>,
    val selectedLanguage: Locale,
    val postVisibility: StatusVisibility,
    val interactionSetting: PostInteractionSetting,
    val sensitive: Boolean,
    val warningContent: TextFieldValue,
) {

    val mediaAvailableCount: Int
        get() = globalRules.maxMediaCount - medias.size

    val showPostVisibilitySetting: Boolean
        get() = allAccounts.any { it.account.platform.protocol.isActivityPub }

    val showInteractionSetting: Boolean
        get() = allAccounts.any { it.account.platform.protocol.isBluesky }

    companion object {

        fun default(): MultiAccountPublishingUiState {
            return MultiAccountPublishingUiState(
                addedAccounts = emptyList(),
                allAccounts = emptyList(),
                publishing = false,
                content = TextFieldValue(""),
                globalRules = defaultRules(),
                medias = emptyList(),
                selectedLanguage = getDefaultLocale(),
                postVisibility = StatusVisibility.PUBLIC,
                interactionSetting = PostInteractionSetting.default(),
                sensitive = false,
                warningContent = TextFieldValue(""),
            )
        }

        fun defaultRules(): PublishBlogRules {
            return PublishBlogRules(
                maxCharacters = 120,
                maxMediaCount = 4,
                maxPollOptions = 0,
                supportPoll = false,
                supportSpoiler = false,
                maxLanguageCount = 1,
            )
        }
    }
}

data class MultiPublishingAccountUiState(
    val account: LoggedAccount,
    val rules: PublishBlogRules,
)

data class MultiPublishingAccountWithRules(
    val account: LoggedAccount,
    val rules: PublishBlogRules?,
)

internal val StatusVisibility.describeStringId: StringResource
    get() = when (this) {
        StatusVisibility.PUBLIC -> Res.string.post_status_scope_public
        StatusVisibility.UNLISTED -> Res.string.post_status_scope_unlisted
        StatusVisibility.PRIVATE -> Res.string.post_status_scope_follower_only
        StatusVisibility.DIRECT -> Res.string.post_status_scope_mentioned_only
    }

data class PublishPostMediaAttachmentFile(
    val file: ContentProviderFile,
    override val isVideo: Boolean,
    override val alt: String?,
) : PublishPostMedia {

    override val uri: String
        get() = file.uri.toString()

}
