package com.zhangke.fread.status.ui.source

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.status.search.SearchedPlatform

@Composable
fun SearchPlatformResultUi(
    searchedResult: SearchedPlatform,
    onContentClick: (SearchedPlatform) -> Unit,
) {
    when (searchedResult) {
        is SearchedPlatform.Platform -> BlogPlatformUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onContentClick(searchedResult)
                },
            platform = searchedResult.platform,
        )

        is SearchedPlatform.Snapshot -> BlogPlatformSnapshotUi(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onContentClick(searchedResult) },
            platform = searchedResult.snapshot,
        )
    }
}
