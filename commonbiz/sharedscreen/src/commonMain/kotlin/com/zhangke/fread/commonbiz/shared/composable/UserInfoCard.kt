package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.RelationshipUiState
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.status.ui.richtext.SelectableRichText
import com.zhangke.fread.status.ui.user.UserHandleLine
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.img_banner_background
import org.jetbrains.compose.resources.painterResource

private const val BANNER_ASPECT = 3F

@Composable
fun UserInfoCard(
    modifier: Modifier,
    user: BlogAuthor,
    relationship: RelationshipUiState?,
    onUserClick: (BlogAuthor) -> Unit,
    onFollowAccountClick: (BlogAuthor) -> Unit,
    onUnfollowAccountClick: (BlogAuthor) -> Unit,
    onAcceptClick: (BlogAuthor) -> Unit,
    onRejectClick: (BlogAuthor) -> Unit,
    onCancelFollowRequestClick: (BlogAuthor) -> Unit,
    onUnblockClick: (BlogAuthor) -> Unit,
) {
    Box(modifier = modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
            ) {
                AutoSizeImage(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .aspectRatio(BANNER_ASPECT)
                        .noRippleClick { onUserClick(user) },
                    url = user.avatar.orEmpty(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    placeholderPainter = {
                        painterResource(Res.drawable.img_banner_background)
                    },
                    errorPainter = {
                        painterResource(Res.drawable.img_banner_background)
                    },
                )
                val avatarContainerSize = 86.dp
                val overLapHeight = 16.dp
                val avatarSize = 58.dp
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(avatarContainerSize)
                        .offset(y = -overLapHeight)
                        .padding(start = 16.dp),
                ) {
                    BlogAuthorAvatar(
                        modifier = Modifier
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                shape = CircleShape,
                            )
                            .clickable { onUserClick(user) }
                            .size(avatarSize),
                        imageUrl = user.avatar.orEmpty(),
                    )
                    Column(
                        modifier = Modifier.padding(start = avatarSize, top = overLapHeight)
                            .padding(start = 16.dp, top = 8.dp)
                    ) {
                        SelectableRichText(
                            modifier = Modifier,
                            richText = user.humanizedName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSizeSp = 18F,
                            onUrlClick = {},
                        )
                        UserHandleLine(
                            modifier = Modifier.padding(top = 4.dp),
                            handle = user.prettyHandle,
                            bot = user.bot,
                            followedBy = relationship == RelationshipUiState.FOLLOWED_BY,
                        )
                    }
                }

                if (relationship != null) {
                    RelationshipStateButton(
                        modifier = Modifier,
                        relationship = relationship,
                        onFollowClick = { onFollowAccountClick(user) },
                        onUnfollowClick = { onUnfollowAccountClick(user) },
                        onAcceptClick = { onAcceptClick(user) },
                        onRejectClick = { onRejectClick(user) },
                        onCancelFollowRequestClick = { onCancelFollowRequestClick(user) },
                        onUnblockClick = { onUnblockClick(user) },
                    )
                }

                SelectableRichText(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            top = 6.dp,
                            end = 16.dp,
                        )
                        .fillMaxWidth(),
                    richText = user.humanizedDescription,
                    onUrlClick = {},
                    onMaybeHashtagClick = {},
                )

                if (user.followingCount != null && user.followersCount != null) {
                    Box(modifier = Modifier.padding(start = 16.dp, top = 4.dp)) {
                        UserFollowLine(
                            modifier = Modifier,
                            followersCount = user.followersCount,
                            followingCount = user.followingCount,
                            statusesCount = user.statusesCount,
                            onFollowerClick = {},
                            onFollowingClick = {},
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
