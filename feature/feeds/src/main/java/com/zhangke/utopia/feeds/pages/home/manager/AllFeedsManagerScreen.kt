package com.zhangke.utopia.feeds.pages.home.manager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.AvatarHorizontalStack
import com.zhangke.framework.composable.theme.TopAppBarDefault
import com.zhangke.framework.ktx.isSingle
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsPageUiState
import com.zhangke.utopia.feeds.pages.manager.single.SingleFeedsManagerScreen

@Composable
internal fun AllFeedsManagerScreen(
    feedsList: List<FeedsPageUiState>
) {
    val navigator = LocalNavigator.currentOrThrow
    AllFeedsManagerScreenContent(
        feedsList = feedsList,
        onAddFeedsClick = {
            navigator.push(SingleFeedsManagerScreen(addMode = true))
        },
    )
}

@Composable
private fun AllFeedsManagerScreenContent(
    feedsList: List<FeedsPageUiState>,
    onAddFeedsClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(TopAppBarDefault.TopBarHeight)
                .padding(
                    start = TopAppBarDefault.StartPadding,
                    end = TopAppBarDefault.EndPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Feeds Manager",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.weight(1F))
            IconButton(
                onClick = onAddFeedsClick,
            ) {
                Icon(
                    painter = rememberVectorPainter(Icons.Default.Add),
                    contentDescription = "Add New Feeds",
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            horizontalArrangement = Arrangement.Center,
        ) {
            items(feedsList) { uiState ->
                AllFeedsManagerItem(
                    modifier = Modifier.fillMaxWidth(),
                    uiState,
                )
            }
        }
    }
}

@Composable
private fun AllFeedsManagerItem(
    modifier: Modifier = Modifier,
    uiState: FeedsPageUiState,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Card(
            elevation = CardDefaults.cardElevation(3.dp),
        ) {
            Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                if (uiState.serverList.isSingle()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        val serverInfo = uiState.serverList.first()
                        val serverThumbnail = serverInfo.thumbnail
                        if (serverThumbnail.isNullOrEmpty().not()) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape),
                                model = uiState.serverList.first().thumbnail,
                                contentScale = ContentScale.Crop,
                                contentDescription = "ServerAvatar",
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = serverInfo.name,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                } else {
                    val avatarList = uiState.serverList.mapNotNull { it.thumbnail }.take(5)
                    AvatarHorizontalStack(
                        avatars = avatarList,
                        avatarSize = 20.dp,
                    )
                }

                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = uiState.name,
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = "Source count: ${uiState.sourceList.size}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
