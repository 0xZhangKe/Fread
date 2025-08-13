package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.VerticalIndentLayout
import com.zhangke.framework.utils.HighlightTextBuildUtil
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.Relationships
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
        description = user.humanizedDescription,
        followersCount = user.followersCount,
        followingCount = user.followingCount,
        statusesCount = user.statusesCount,
        relationships = user.relationships,
        onProfileClick = { onUserClick(user) },
        onFollowAccountClick = { onFollowAccountClick(user) },
        onUnfollowAccountClick = { onUnfollowAccountClick(user) },
        onUnblockClick = { onUnblockClick(user) },
        onCancelFollowRequestClick = { onCancelFollowRequestClick(user) },
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
    relationships: Relationships?,
    onProfileClick: () -> Unit,
    onFollowAccountClick: () -> Unit,
    onUnfollowAccountClick: () -> Unit,
    onUnblockClick: () -> Unit,
    onCancelFollowRequestClick: () -> Unit,
) {
    Box(modifier = modifier.clickable { onProfileClick() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val avatarContainerSize = 70.dp
            val overLapHeight = 16.dp
            val avatarSize = 68.dp
            VerticalIndentLayout(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                indentHeight = overLapHeight,
                headerContent = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .aspectRatio(BANNER_ASPECT),
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
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(avatarContainerSize)
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
                                    .padding(start = 8.dp, top = 2.dp)
                            ) {
                                SelectableRichText(
                                    modifier = Modifier,
                                    richText = nickName,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSizeSp = 18F,
                                    onUrlClick = {},
                                )
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
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 2.dp),
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
                            if (relationships != null) {
                                RelationshipStateButton(
                                    modifier = Modifier,
                                    relationship = relationships,
                                    onFollowClick = { onFollowAccountClick() },
                                    onUnfollowClick = { onUnfollowAccountClick() },
                                    onUnblockClick = { onUnblockClick() },
                                    onCancelFollowRequestClick = { onCancelFollowRequestClick() },
                                )
                            } else {
                                Box(modifier = Modifier.size(height = 28.dp, width = 8.dp))
                            }
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
