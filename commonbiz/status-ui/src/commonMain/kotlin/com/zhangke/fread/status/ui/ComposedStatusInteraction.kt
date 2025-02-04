package com.zhangke.fread.status.ui

import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusActionType


interface ComposedStatusInteraction {

    fun onStatusInteractive(status: StatusUiState, type: StatusActionType)
    fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor)
    fun onVoted(status: StatusUiState, blogPollOptions: List<BlogPoll.Option>)
    fun onHashtagInStatusClick(role: IdentityRole, hashtagInStatus: HashtagInStatus)
    fun onHashtagClick(role: IdentityRole, tag: Hashtag)
    fun onMentionClick(role: IdentityRole, mention: Mention)
    fun onMentionClick(role: IdentityRole, did: String, protocol: StatusProviderProtocol)
    fun onStatusClick(status: StatusUiState)
    fun onBlockClick(role: IdentityRole, blog: Blog)
    fun onFollowClick(role: IdentityRole, target: BlogAuthor)
    fun onUnfollowClick(role: IdentityRole, target: BlogAuthor)
    fun onBoostedClick(role: IdentityRole, status: StatusUiState)
    fun onFavouritedClick(role: IdentityRole, status: StatusUiState)
    fun onTranslateClick(role: IdentityRole, status: StatusUiState)
    fun onShowOriginalClick(status: StatusUiState)
}
