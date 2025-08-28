package com.zhangke.fread.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.activitypub.app.internal.model.UserUriInsights
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.richtext.RichText

data class UserDetailUiState(
    val locator: PlatformLocator,
    val loading: Boolean,
    val userInsight: UserUriInsights?,
    val accountUiState: UserDetailAccountUiState?,
    val personalNote: String?,
    val relationships: Relationships?,
    val domainBlocked: Boolean?,
    val isAccountOwner: Boolean,
)

data class UserDetailAccountUiState(
    val account: ActivityPubAccountEntity,
    val userName: RichText,
    val description: RichText,
    val emojis: List<Emoji>,
)
