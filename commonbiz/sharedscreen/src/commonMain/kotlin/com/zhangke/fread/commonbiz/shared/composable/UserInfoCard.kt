package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.zhangke.framework.composable.VerticalIndentLayout
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
            val avatarContainerSize = 70.dp
            val overLapHeight = 16.dp
            val avatarSize = 68.dp
            VerticalIndentLayout(
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                indentHeight = overLapHeight,
                headerContent = {
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
                                    .clickable { onUserClick(user) }
                                    .size(avatarSize),
                                imageUrl = user.avatar.orEmpty(),
                            )
                            Column(
                                modifier = Modifier.padding(start = avatarSize, top = overLapHeight)
                                    .padding(start = 8.dp, top = 2.dp)
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
                                    modifier = Modifier.padding(top = 1.dp),
                                    handle = user.prettyHandle,
                                    bot = user.bot,
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
                            richText = user.humanizedDescription,
                            onUrlClick = {},
                            onMaybeHashtagClick = {},
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            if (user.followingCount != null && user.followersCount != null) {
                                Box(modifier = Modifier.weight(1F)) {
                                    UserFollowLine(
                                        modifier = Modifier,
                                        isHighlightBigger = false,
                                        followersCount = user.followersCount,
                                        followingCount = user.followingCount,
                                        statusesCount = user.statusesCount,
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.weight(1F))
                            }

                            if (user.relationships != null) {
                                RelationshipStateButton(
                                    modifier = Modifier,
                                    relationship = user.relationships!!,
                                    onFollowClick = { onFollowAccountClick(user) },
                                    onUnfollowClick = { onUnfollowAccountClick(user) },
                                    onAcceptClick = { onAcceptClick(user) },
                                    onRejectClick = { onRejectClick(user) },
                                    onCancelFollowRequestClick = { onCancelFollowRequestClick(user) },
                                    onUnblockClick = { onUnblockClick(user) },
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
