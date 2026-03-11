package com.zhangke.fread.feeds.pages.manager.add.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.resources.blueskyDescription
import com.zhangke.fread.common.resources.blueskyLogo
import com.zhangke.fread.common.resources.blueskyName
import com.zhangke.fread.common.resources.mastodonDescription
import com.zhangke.fread.common.resources.mastodonHorizontalLogo
import com.zhangke.fread.common.resources.mastodonLogo
import com.zhangke.fread.common.resources.mastodonName
import com.zhangke.fread.common.resources.mixedDescription
import com.zhangke.fread.common.resources.mixedName
import com.zhangke.fread.common.resources.rssLogo
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.img_add_content_bsky
import com.zhangke.fread.feeds.img_add_content_mastodon
import com.zhangke.fread.feeds.img_add_content_mixed
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
object SelectContentTypeScreenNavKey : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectContentTypeScreen(viewModel: SelectContentTypeViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val mastodonCardColor = Color(0xFFFFA600)
    val blueskyCardColor = Color(0xFF0080FF)
    val mixedCardColor = Color(0xFF3AD06B)
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.feedsSelectTypeScreenTitle),
                onBackClick = backStack::removeLastOrNull,
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ContentTypeCard(
                modifier = Modifier.fillMaxWidth(),
                cardColor = mastodonCardColor,
                title = mastodonName(),
                description = mastodonDescription(),
                onCardClick = viewModel::onMastodonClick,
                contentImage = {
                    Image(
                        modifier = Modifier.fillMaxWidth(fraction = 0.5F)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_mastodon),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
            )
            ContentTypeCard(
                modifier = Modifier.fillMaxWidth(),
                cardColor = blueskyCardColor,
                title = blueskyName(),
                description = blueskyDescription(),
                onCardClick = viewModel::onBlueskyClick,
                contentImage = {
                    Image(
                        modifier = Modifier.fillMaxWidth(fraction = 0.5F)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_bsky),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
            )
            ContentTypeCard(
                modifier = Modifier.fillMaxWidth(),
                cardColor = mixedCardColor,
                title = mixedName(),
                description = mixedDescription(),
                onCardClick = { backStack.add(AddMixedFeedsScreenNavKey) },
                contentImage = {
                    Image(
                        modifier = Modifier.fillMaxWidth(fraction = 0.5F)
                            .fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_mixed),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                },
            )
        }
    }
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    ConsumeFlow(viewModel.finishPageFlow) { backStack.removeLastOrNull() }
    LaunchedEffect(Unit) {
        viewModel.onPageResumed(this)
    }
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
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier.align(Alignment.Center)
                .fillMaxWidth(),
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

@Composable
private fun ContentTypeCard(
    modifier: Modifier,
    cardColor: Color,
    contentImage: @Composable BoxScope.() -> Unit,
    title: String,
    description: String,
    onCardClick: () -> Unit,
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(180.dp),
        onClick = onCardClick,
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            contentImage()
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.8F)
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 22.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
