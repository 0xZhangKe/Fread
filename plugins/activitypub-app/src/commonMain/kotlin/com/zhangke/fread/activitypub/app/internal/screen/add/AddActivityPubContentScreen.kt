package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.emoji_celebrate
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.source.BlogPlatformCard
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Serializable
data class AddActivityPubContentScreenKey(val platform: BlogPlatform) : NavKey

@Composable
fun AddActivityPubContentScreen(
    platform: BlogPlatform,
    viewModel: AddActivityPubContentViewModel,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    AddActivityPubContentContent(
        platform = platform,
        onBackClick = { backStack.removeLastOrNull() },
        onLoginClick = {
            viewModel.onLoginClick()
            backStack.removeLastOrNull()
        },
    )
}

@Composable
private fun AddActivityPubContentContent(
    platform: BlogPlatform,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.addContentTitle),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp),
        ) {
            ContentAddingState(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            PlatformPreview(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(),
                platform = platform,
                onLoginClick = onLoginClick,
            )
            Spacer(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
private fun ContentAddingState(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val scale = remember { Animatable(0.1F) }
        LaunchedEffect(Unit) {
            delay(100)
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 600,
                    easing = EaseOutBack,
                )
            )
        }
        Image(
            modifier = Modifier.size(68.dp)
                .scale(scale.value),
            painter = painterResource(Res.drawable.emoji_celebrate),
            contentDescription = null,
        )
        Text(
            text = stringResource(LocalizedString.contentAddSuccess),
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun PlatformPreview(
    modifier: Modifier,
    platform: BlogPlatform,
    onLoginClick: () -> Unit,
) {
    BlogPlatformCard(
        modifier = modifier,
        platform = platform,
        onLoginClick = onLoginClick,
    )
}
