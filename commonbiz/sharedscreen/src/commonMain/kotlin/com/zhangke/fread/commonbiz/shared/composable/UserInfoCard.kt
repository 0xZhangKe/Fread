package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.VerticalIndentLayout
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.richtext.SelectableRichText
import com.zhangke.fread.status.ui.user.UserHandleLine
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.img_banner_background
import com.zhangke.fread.statusui.status_ui_user_detail_follower_info
import com.zhangke.fread.statusui.status_ui_user_detail_following_info
import com.zhangke.fread.statusui.status_ui_user_detail_posts
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val BANNER_ASPECT = 3F

@Composable
fun UserInfoCard(
    modifier: Modifier,
    user: BlogAuthor,
    onUserClick: (BlogAuthor) -> Unit,
    onFollowAccountClick: (BlogAuthor) -> Unit,
    onUnfollowAccountClick: (BlogAuthor) -> Unit,
    onUnblockClick: (BlogAuthor) -> Unit,
    onCancelFollowRequestClick: (BlogAuthor) -> Unit,
) {
    BasicProfileCard(
        modifier = modifier,
        banner = user.banner.orEmpty(),
        avatar = user.avatar.orEmpty(),
        nickName = user.humanizedName,
        prettyHandle = user.prettyHandle,
        bot = user.bot,
        showActiveState = false,
        description = user.humanizedDescription,
        followersCount = user.followersCount,
        followingCount = user.followingCount,
        statusesCount = user.statusesCount,
        actionButton = if (user.relationships != null) {
            {
                RelationshipStateButton(
                    modifier = Modifier,
                    relationship = user.relationships!!,
                    onFollowClick = { onFollowAccountClick(user) },
                    onUnfollowClick = { onUnfollowAccountClick(user) },
                    onUnblockClick = { onUnblockClick(user) },
                    onCancelFollowRequestClick = { onCancelFollowRequestClick(user) },
                )
            }
        } else {
            null
        },
        onProfileClick = { onUserClick(user) },
    )
}

@Composable
fun UserInfoCard(
    modifier: Modifier,
    user: BlogAuthor,
    showActiveState: Boolean,
    onUserClick: (BlogAuthor) -> Unit,
    actionButton: (@Composable () -> Unit)?,
    bottomPanel: (@Composable () -> Unit)? = null,
) {
    BasicProfileCard(
        modifier = modifier,
        banner = user.banner.orEmpty(),
        avatar = user.avatar.orEmpty(),
        nickName = user.humanizedName,
        prettyHandle = user.prettyHandle,
        bot = user.bot,
        showActiveState = showActiveState,
        description = user.humanizedDescription,
        followersCount = user.followersCount,
        followingCount = user.followingCount,
        statusesCount = user.statusesCount,
        actionButton = actionButton,
        onProfileClick = { onUserClick(user) },
        bottomPanel = bottomPanel,
    )
}

@Composable
fun BasicProfileCard(
    modifier: Modifier,
    banner: String,
    avatar: String,
    nickName: RichText,
    prettyHandle: String,
    bot: Boolean,
    description: RichText,
    followersCount: Long?,
    followingCount: Long?,
    statusesCount: Long?,
    actionButton: (@Composable () -> Unit)?,
    showActiveState: Boolean,
    onProfileClick: () -> Unit,
    bottomPanel: (@Composable () -> Unit)? = null,
) {
    Box(modifier = modifier.clickable { onProfileClick() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val avatarContainerHeight = 74.dp
            val overLapHeight = 22.dp
            val avatarSize = 68.dp
            VerticalIndentLayout(
                modifier = Modifier.fillMaxWidth(),
                indentHeight = overLapHeight,
                headerContent = {
                    Box(
                        modifier = Modifier.fillMaxWidth().aspectRatio(BANNER_ASPECT),
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(Res.drawable.img_banner_background),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                        AutoSizeImage(
                            modifier = Modifier.fillMaxSize(),
                            url = banner,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            placeholderPainter = {
                                painterResource(Res.drawable.img_banner_background)
                            },
                            errorPainter = {
                                painterResource(Res.drawable.img_banner_background)
                            },
                        )
                    }
                },
                indentContent = {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(avatarContainerHeight)
                                .padding(start = 8.dp),
                        ) {
                            BlogAuthorAvatar(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        shape = CircleShape,
                                    )
                                    .size(avatarSize),
                                imageUrl = avatar,
                            )
                            Column(
                                modifier = Modifier.padding(start = avatarSize, top = overLapHeight)
                                    .padding(start = 8.dp, top = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    SelectableRichText(
                                        modifier = Modifier,
                                        richText = nickName,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSizeSp = 18F,
                                        onUrlClick = {},
                                    )
                                    if (showActiveState) {
                                        Box(
                                            modifier = Modifier.padding(start = 4.dp)
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF22C55E))
                                        )
                                    }
                                }
                                UserHandleLine(
                                    modifier = Modifier.padding(top = 1.dp),
                                    handle = prettyHandle,
                                    bot = bot,
                                    followedBy = false,
                                )
                            }
                        }

                        SelectableRichText(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                )
                                .fillMaxWidth(),
                            richText = description,
                            onUrlClick = {},
                            onMaybeHashtagClick = {},
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (followingCount != null && followersCount != null) {
                                UserFollowInfo(
                                    modifier = Modifier.weight(1F),
                                    followersCount = followersCount,
                                    followingCount = followingCount,
                                    statusesCount = statusesCount,
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1F))
                            }
                            if (actionButton != null) {
                                actionButton()
                            } else {
                                Box(modifier = Modifier.size(height = 28.dp, width = 8.dp))
                            }
                        }
                        if (bottomPanel != null) {
                            bottomPanel()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun UserFollowInfo(
    modifier: Modifier,
    followersCount: Long?,
    followingCount: Long?,
    statusesCount: Long?,
) {
    val followerText = stringResource(Res.string.status_ui_user_detail_follower_info)
    val followingText = stringResource(Res.string.status_ui_user_detail_following_info)
    val statusText = stringResource(Res.string.status_ui_user_detail_posts)
    val infoText = remember(followingCount, followersCount, statusesCount) {
        buildString {
            if (followersCount != null) {
                append(HighlightTextBuildUtil.HIGHLIGHT_START_SYMBOL)
                append(followersCount.formatToHumanReadable())
                append(HighlightTextBuildUtil.HIGHLIGHT_END_SYMBOL)
                append(' ')
                append(followerText)
            }
            if (followingCount != null) {
                if (followersCount != null) {
                    append(" · ")
                }
                append(HighlightTextBuildUtil.HIGHLIGHT_START_SYMBOL)
                append(followingCount.formatToHumanReadable())
                append(HighlightTextBuildUtil.HIGHLIGHT_END_SYMBOL)
                append(' ')
                append(followingText)
            }
            if (statusesCount != null) {
                if (followersCount != null || followingCount != null) {
                    append(" · ")
                }
                append(HighlightTextBuildUtil.HIGHLIGHT_START_SYMBOL)
                append(statusesCount.formatToHumanReadable())
                append(HighlightTextBuildUtil.HIGHLIGHT_END_SYMBOL)
                append(' ')
                append(statusText)
            }
        }.let {
            HighlightTextBuildUtil.buildHighlightText(
                text = it,
                fontWeight = FontWeight.Bold,
            )
        }
    }
    Text(
        text = infoText,
        modifier = modifier,
        style = MaterialTheme.typography.bodySmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
