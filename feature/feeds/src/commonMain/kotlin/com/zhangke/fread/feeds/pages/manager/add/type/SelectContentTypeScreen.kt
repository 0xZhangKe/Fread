package com.zhangke.fread.feeds.pages.manager.add.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.resources.blueskyDescription
import com.zhangke.fread.common.resources.blueskyLogo
import com.zhangke.fread.common.resources.blueskyName
import com.zhangke.fread.common.resources.mastodonDescription
import com.zhangke.fread.common.resources.mastodonHorizontalLogo
import com.zhangke.fread.common.resources.mastodonLogo
import com.zhangke.fread.common.resources.mixedDescription
import com.zhangke.fread.common.resources.mixedName
import com.zhangke.fread.common.resources.rssLogo
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.feeds_select_type_screen_title
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreen
import org.jetbrains.compose.resources.stringResource

class SelectContentTypeScreen : BaseScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<SelectContentTypeViewModel>()
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.feeds_select_type_screen_title),
                    onBackClick = navigator::pop,
                )
            },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                MastodonContentCard(
                    modifier = Modifier.padding(top = 46.dp)
                        .fillMaxWidth(),
                    onClick = viewModel::onMastodonClick,
                )
                BlueskyContentCard(
                    modifier = Modifier.padding(top = 16.dp)
                        .fillMaxWidth(),
                    onClick = viewModel::onBlueskyClick,
                )
                MixedContentCard(
                    modifier = Modifier.padding(top = 16.dp)
                        .fillMaxWidth(),
                    onClick = { navigator.push(AddMixedFeedsScreen()) },
                )
            }
        }
        ConsumeOpenScreenFlow(viewModel.openScreenFlow)
        ConsumeFlow(viewModel.finishPageFlow) { navigator.pop() }
    }

    @Composable
    private fun MastodonContentCard(
        modifier: Modifier,
        onClick: () -> Unit,
    ) {
        TypeCardContainer(
            modifier = modifier,
            onClick = onClick,
            cardFrontColor = Color(0xFF5C4DE3),
            logo = {
                Image(
                    modifier = Modifier.width(160.dp),
                    imageVector = mastodonHorizontalLogo(),
                    contentDescription = null,
                )
            },
            description = {
                Text(
                    text = mastodonDescription(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            },
        )
    }

    @Composable
    private fun BlueskyContentCard(
        modifier: Modifier,
        onClick: () -> Unit,
    ) {
        TypeCardContainer(
            modifier = modifier,
            onClick = onClick,
            cardFrontColor = Color(0xFF0285FF),
            logo = {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier.size(38.dp),
                        imageVector = blueskyLogo(),
                        contentDescription = null,
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = blueskyName(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            description = {
                Text(
                    text = blueskyDescription(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            },
        )
    }

    @Composable
    private fun MixedContentCard(
        modifier: Modifier,
        onClick: () -> Unit,
    ) {
        TypeCardContainer(
            modifier = modifier,
            onClick = onClick,
            cardFrontColor = MaterialTheme.colorScheme.primary,
            logo = {
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MixedLogos()
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = mixedName(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            },
            description = {
                Text(
                    text = mixedDescription(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                )
            },
        )
    }

    @Composable
    private fun MixedLogos() {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val avatarSize = 20.dp
            Image(
                modifier = Modifier.size(avatarSize),
                imageVector = mastodonLogo(),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            Image(
                modifier = Modifier.padding(start = 6.dp)
                    .size(avatarSize),
                imageVector = blueskyLogo(),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
            Image(
                modifier = Modifier.padding(start = 6.dp)
                    .size(avatarSize),
                imageVector = rssLogo(),
                contentScale = ContentScale.Fit,
                contentDescription = null,
            )
        }
    }

    @Composable
    private fun TypeCardContainer(
        modifier: Modifier,
        cardFrontColor: Color,
        onClick: () -> Unit,
        logo: @Composable () -> Unit,
        description: @Composable () -> Unit,
    ) {
        Box(modifier = modifier) {
            Card(
                modifier = Modifier.align(Alignment.Center)
                    .fillMaxWidth(0.6F),
                onClick = onClick,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(
                    modifier = Modifier.background(cardFrontColor.copy(alpha = 0.2F))
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    logo()
                    Box(modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)) {
                        description()
                    }
                }
            }
        }
    }
}
