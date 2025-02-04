package com.zhangke.fread.debug.screens.media

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.ktx.second
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.ui.media.BlogMedias

class DoubleImageTestScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val list = remember {
            mockDoubleImageMediaList()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = "DoubleImageMedia",
                    onBackClick = navigator::pop,
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
                contentPadding = PaddingValues(15.dp),
            ) {
                items(list) { item ->
                    val label = "${item.first().fetchDebugAspectRatio()}, ${
                        item.second().fetchDebugAspectRatio()
                    }"
                    MockBlog(label) {
                        BlogMedias(
                            modifier = Modifier
                                .fillMaxWidth(),
                            mediaList = item,
                            blogTranslationState = BlogTranslationUiState(support = false),
                            indexInList = 1,
                            sensitive = false,
                            onMediaClick = { },
                        )
                    }
                }
            }
        }
    }
}

private const val mockImageUrl1 =
    "https://media.cmx.edu.kg/cache/media_attachments/files/110/909/368/788/194/340/original/eebdc8ce2a725959.jpeg"

private const val mockImageUrl2 =
    "https://media.cmx.edu.kg/media_attachments/files/110/911/160/697/759/594/original/ed8731d7676b35a4.png"

private fun mockDoubleImageMediaList(): List<List<BlogMedia>> {
    return listOf(
        // horizontal arrange
        doubleImageMedia(1F, 1F),
        doubleImageMedia(0.3F, 0.3F),
        doubleImageMedia(0.3F, 0.6F),
        doubleImageMedia(0.7F, 0.3F),
        doubleImageMedia(7F, 0.3F),
        doubleImageMedia(0.8F, 0.8F),
        // vertical arrange
        doubleImageMedia(2F, 2F),
        doubleImageMedia(10F, 1.2F),
        doubleImageMedia(1.3F, 10F),
        doubleImageMedia(1.7F, 1.7F),
        doubleImageMedia(1.7F, 12F),
    )
}

private fun doubleImageMedia(firstAspect: Float, secondAspect: Float): List<BlogMedia> {
    return listOf(
        mockBlogImageMedia(mockImageUrl1, firstAspect),
        mockBlogImageMedia(mockImageUrl2, secondAspect)
    )
}
