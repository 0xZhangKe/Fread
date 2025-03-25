package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_detail_creator_prefix
import com.zhangke.fread.bluesky.bsky_feeds_explorer_liked_by
import com.zhangke.fread.bluesky.internal.composable.FeedsAvatar
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.model.BlueskyProfile
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.action.likeAlt
import com.zhangke.fread.status.ui.action.likeIcon
import com.zhangke.fread.status.ui.action.pinAlt
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

class FeedsDetailScreen(
    private val feedsJson: String,
    private val role: IdentityRole,
) : BaseScreen() {

    companion object {

        fun create(feeds: BlueskyFeeds, role: IdentityRole): FeedsDetailScreen {
            return FeedsDetailScreen(
                role = role,
                feedsJson = globalJson.encodeToString(
                    serializer = BlueskyFeeds.serializer(),
                    value = feeds,
                ),
            )
        }
    }

    @Transient
    var onFeedsUpdate: ((BlueskyFeeds.Feeds) -> Unit)? = null

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<FeedsDetailViewModel, FeedsDetailViewModel.Factory> {
            val feeds = globalJson.decodeFromString<BlueskyFeeds>(feedsJson)
            it.create(role, feeds)
        }
        val uiState by viewModel.uiState.collectAsState()
        val textHandler = LocalActivityTextHandler.current
        val snackbarHostState = rememberSnackbarHostState()
        FeedsDetailContent(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onCreatorClick = { navigator.push(BskyUserDetailScreen(role = role, did = it.did)) },
            onShareFeedsClick = { textHandler.shareUrl(url = it.uri, text = it.displayName) },
            onShareListClick = { textHandler.shareUrl(url = it.uri, text = it.name) },
            onLikeClick = viewModel::onLikeClick,
            onPinClick = viewModel::onPinClick,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessageFlow)
        ConsumeFlow(viewModel.feedsUpdateFlow) {
            onFeedsUpdate?.invoke(it)
        }
    }

    @Composable
    private fun FeedsDetailContent(
        uiState: FeedsDetailUiState,
        snackbarHostState: SnackbarHostState,
        onCreatorClick: (BlueskyProfile) -> Unit,
        onShareFeedsClick: (BlueskyFeeds.Feeds) -> Unit,
        onShareListClick: (BlueskyFeeds.List) -> Unit,
        onLikeClick: () -> Unit,
        onPinClick: () -> Unit,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 32.dp)) {
                val feeds = uiState.feeds
                if (feeds is BlueskyFeeds.Feeds) {
                    FeedsDetail(
                        feeds = feeds,
                        onCreatorClick = onCreatorClick,
                        onShareClick = onShareFeedsClick,
                        onLikeClick = onLikeClick,
                        onPinClick = onPinClick,
                    )
                } else if (feeds is BlueskyFeeds.List) {
                    ListDetail(
                        feeds = feeds,
                        onShareClick = onShareListClick,
                    )
                }
            }
            SnackbarHost(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                hostState = snackbarHostState,
            )
        }
    }

    @Composable
    private fun FeedsDetail(
        feeds: BlueskyFeeds.Feeds,
        onCreatorClick: (BlueskyProfile) -> Unit,
        onShareClick: (BlueskyFeeds.Feeds) -> Unit,
        onLikeClick: () -> Unit,
        onPinClick: () -> Unit,
    ) {
        FeedsBasicInfo(
            name = feeds.displayName(),
            authorHandle = feeds.creator.prettyHandle,
            avatar = feeds.avatar,
            description = feeds.description,
            onShareClick = { onShareClick(feeds) },
            onCreatorClick = { onCreatorClick(feeds.creator) },
        )
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp),
            text = stringResource(
                Res.string.bsky_feeds_explorer_liked_by,
                (feeds.likeCount ?: 0L).formatToHumanReadable(),
            ),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onLikeClick,
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = likeIcon(liked = feeds.liked),
                    contentDescription = likeAlt(),
                    tint = if (feeds.liked) {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            val pinBtnColors = if (feeds.pinned) {
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = Color.White,
                )
            } else {
                ButtonDefaults.textButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White,
                )
            }
            TextButton(
                modifier = Modifier.weight(1F),
                onClick = onPinClick,
                colors = pinBtnColors,
            ) {
                Text(pinAlt(feeds.pinned))
            }
        }
    }


    @Composable
    private fun ListDetail(
        feeds: BlueskyFeeds.List,
        onShareClick: (BlueskyFeeds.List) -> Unit,
    ) {
        FeedsBasicInfo(
            name = feeds.name,
            avatar = feeds.avatar,
            authorHandle = null,
            description = feeds.description,
            onShareClick = { onShareClick(feeds) },
            onCreatorClick = {},
        )
    }

    @Composable
    private fun FeedsBasicInfo(
        avatar: String?,
        name: String,
        authorHandle: String?,
        description: String?,
        onShareClick: () -> Unit,
        onCreatorClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FeedsAvatar(
                url = avatar,
                modifier = Modifier,
            )
            Column(
                modifier = Modifier.weight(1F).padding(start = 16.dp),
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.SemiBold),
                )
                if (!authorHandle.isNullOrEmpty()) {
                    val authorPrefix =
                        stringResource(Res.string.bsky_feeds_detail_creator_prefix)
                    val creator = remember(authorHandle) {
                        buildAnnotatedString {
                            append(authorPrefix)
                            append(" ")
                            append(authorHandle)
                            addStyle(
                                style = SpanStyle(textDecoration = TextDecoration.Underline),
                                start = authorPrefix.length,
                                end = length,
                            )
                        }
                    }
                    Text(
                        modifier = Modifier.noRippleClick { onCreatorClick() }
                            .padding(top = 1.dp),
                        text = creator,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            IconButton(
                onClick = { onShareClick() },
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                )
            }
        }
        if (!description.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp),
                text = description,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
