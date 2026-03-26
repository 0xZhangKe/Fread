package com.zhangke.fread.feeds.pages.manager.add.type

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
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
import com.zhangke.fread.common.resources.blueskyName
import com.zhangke.fread.common.resources.mastodonDescription
import com.zhangke.fread.common.resources.mastodonName
import com.zhangke.fread.common.resources.mixedDescription
import com.zhangke.fread.common.resources.mixedName
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
                modifier = Modifier.padding(top = 22.dp).fillMaxWidth(),
                cardColor = mastodonCardColor,
                title = mastodonName(),
                description = mastodonDescription(),
                onCardClick = viewModel::onMastodonClick,
                contentImage = {
                    Image(
                        modifier = Modifier.fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_mastodon),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
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
                        modifier = Modifier.fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_bsky),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
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
                        modifier = Modifier.fillMaxHeight()
                            .align(Alignment.CenterEnd),
                        painter = painterResource(Res.drawable.img_add_content_mixed),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
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
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .drawBehind {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, cardColor.copy(alpha = 0.9F))
                            )
                        )
                    },
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                        .fillMaxWidth(fraction = 0.9F),
                    textAlign = TextAlign.Start,
                    text = description,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}
