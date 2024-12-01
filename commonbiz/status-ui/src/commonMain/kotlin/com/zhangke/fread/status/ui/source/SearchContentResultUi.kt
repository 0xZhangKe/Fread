package com.zhangke.fread.status.ui.source

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.status.search.SearchContentResult

@Composable
fun SearchContentResultUi(
    content: SearchContentResult,
    onContentClick: (SearchContentResult) -> Unit,
) {
    when (content) {
        is SearchContentResult.Source -> StatusSourceUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onContentClick(content)
                },
            source = content.source,
        )

        is SearchContentResult.ActivityPubPlatform -> BlogPlatformUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContentClick(content) },
            platform = content.platform,
        )

        is SearchContentResult.ActivityPubPlatformSnapshot -> BlogPlatformSnapshotUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContentClick(content) },
            platform = content.platform,
        )

        is SearchContentResult.Bluesky -> BlogPlatformUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContentClick(content) },
            platform = content.platform,
        )
    }
}
