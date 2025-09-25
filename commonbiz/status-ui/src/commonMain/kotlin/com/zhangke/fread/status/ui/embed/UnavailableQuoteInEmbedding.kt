package com.zhangke.fread.status.ui.embed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.BlogEmbed
import org.jetbrains.compose.resources.stringResource

@Composable
fun UnavailableQuoteInEmbedding(
    modifier: Modifier,
    unavailableQuote: BlogEmbed.UnavailableQuote,
    onContentClick: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (!unavailableQuote.blogId.isNullOrEmpty()) {
                    onContentClick(unavailableQuote.blogId!!)
                }
            },
            enabled = !unavailableQuote.blogId.isNullOrEmpty(),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(LocalizedString.status_ui_embed_quote_unavailable),
                )
            }
        }
    }
}
