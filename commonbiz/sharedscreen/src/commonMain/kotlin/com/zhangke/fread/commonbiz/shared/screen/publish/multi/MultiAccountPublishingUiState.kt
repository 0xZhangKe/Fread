package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.ui.text.input.TextFieldValue
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.Locale
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.model.isActivityPub
import com.zhangke.fread.status.model.isBluesky
import com.zhangke.fread.localization.LocalizedString
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

    val hasInputtedData: Boolean
        get() = content.text.isNotEmpty() || medias.isNotEmpty() || warningContent.text.isNotEmpty()

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
                mediaAltMaxCharacters = 1500,
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
        StatusVisibility.PUBLIC -> LocalizedString.postStatusScopePublic
        StatusVisibility.UNLISTED -> LocalizedString.postStatusScopeUnlisted
        StatusVisibility.PRIVATE -> LocalizedString.postStatusScopeFollowerOnly
        StatusVisibility.DIRECT -> LocalizedString.postStatusScopeMentionedOnly
    }

data class PublishPostMediaAttachmentFile(
    val file: ContentProviderFile,
    override val isVideo: Boolean,
    override val alt: String?,
) : PublishPostMedia {

    override val uri: String
        get() = file.uri.toString()

}
