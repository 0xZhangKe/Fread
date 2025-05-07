package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PublishTopBar(
    publishing: Boolean,
    onBackClick: () -> Unit,
    onPublishClick: () -> Unit,
) {
    Toolbar(
        title = stringResource(Res.string.shared_publish_blog_title),
        onBackClick = onBackClick,
        actions = {
            if (publishing) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
            } else {
                SimpleIconButton(
                    onClick = onPublishClick,
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Publish",
                )
            }
        },
    )
}
