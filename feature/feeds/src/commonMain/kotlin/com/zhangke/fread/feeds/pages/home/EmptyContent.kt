package com.zhangke.fread.feeds.pages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.illustration_inspiration
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.empty_content_hint_desc
import com.zhangke.fread.feeds.empty_content_hint_title
import com.zhangke.fread.feeds.feeds_add_content
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyContent(
    modifier: Modifier,
    onAddClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Inside,
                painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.illustration_inspiration),
                contentDescription = null,
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = stringResource(Res.string.empty_content_hint_title),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp),
                text = stringResource(Res.string.empty_content_hint_desc),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }

        Button(
            modifier = Modifier.padding(top = 32.dp),
            onClick = onAddClick
        ) {
            Text(text = stringResource(Res.string.feeds_add_content))
        }
    }
}
