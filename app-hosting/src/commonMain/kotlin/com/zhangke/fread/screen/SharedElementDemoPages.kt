package com.zhangke.fread.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.illustration_cards
import com.zhangke.fread.commonbiz.illustration_celebrate
import com.zhangke.fread.commonbiz.illustration_explorer
import com.zhangke.fread.commonbiz.illustration_inspiration
import com.zhangke.fread.commonbiz.illustration_message
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Serializable
object SharedElementDemoList : NavKey

@Serializable
data class SharedElementDemoDetail(
    val imageId: String,
) : NavKey

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementDemoListPage(
    sharedScope: SharedTransitionScope,
    onImageClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = "Shared Element Demo",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        items(demoImages, key = { it.id }) { image ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { onImageClick(image.id) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(sharedScope) {
                        Image(
                            painter = painterResource(image.drawable),
                            contentDescription = image.description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(image.id),
                                    animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                ),
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = image.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = image.description,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementDemoDetailPage(
    sharedScope: SharedTransitionScope,
    imageId: String,
    onBack: () -> Unit,
) {
    val image = demoImages.firstOrNull { it.id == imageId }
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Back",
            modifier = Modifier.clickable { onBack() },
            style = MaterialTheme.typography.labelLarge,
        )
        if (image == null) {
            Text(
                text = "Image not found",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center,
            ) {
                with(sharedScope) {
                    Image(
                        painter = painterResource(image.drawable),
                        contentDescription = image.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                            .sharedElement(
                                sharedContentState = rememberSharedContentState(image.id),
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            ),
                    )
                }
            }
            Text(
                text = image.title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = image.description,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private data class DemoImage(
    val id: String,
    val drawable: DrawableResource,
    val title: String,
    val description: String,
)

private val demoImages = listOf(
    DemoImage(
        id = "illustration_explorer",
        drawable = Res.drawable.illustration_explorer,
        title = "Explorer",
        description = "Discover new corners of the feed.",
    ),
    DemoImage(
        id = "illustration_message",
        drawable = Res.drawable.illustration_message,
        title = "Message",
        description = "A quiet moment of conversation.",
    ),
    DemoImage(
        id = "illustration_inspiration",
        drawable = Res.drawable.illustration_inspiration,
        title = "Inspiration",
        description = "Ideas with room to breathe.",
    ),
    DemoImage(
        id = "illustration_celebrate",
        drawable = Res.drawable.illustration_celebrate,
        title = "Celebrate",
        description = "Share the highlight together.",
    ),
    DemoImage(
        id = "illustration_cards",
        drawable = Res.drawable.illustration_cards,
        title = "Cards",
        description = "Stories stacked with color.",
    ),
)
