package com.zhangke.utopia.debug.screens.media

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
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.ui.media.BlogMedias

class SingleImageTestScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val list = remember {
            mockSingleImageMediaList()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = "SingleImageMedia",
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
                    MockBlog(item.first().fetchDebugAspectRatio().toString()) {
                        BlogMedias(
                            modifier = Modifier
                                .fillMaxWidth(),
                            mediaList = item,
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

private const val mockImageUrl =
    "https://media.cmx.edu.kg/cache/media_attachments/files/110/909/368/788/194/340/original/eebdc8ce2a725959.jpeg"

private fun mockSingleImageMediaList(): List<List<BlogMedia>> {
    return listOf(
        listOf(mockBlogImageMedia(mockImageUrl, 10F)),
        listOf(mockBlogImageMedia(mockImageUrl, 3F)),
        listOf(mockBlogImageMedia(mockImageUrl, 1F)),
        listOf(mockBlogImageMedia(mockImageUrl, 0.8F)),
        listOf(mockBlogImageMedia(mockImageUrl, 0.7F)),
        listOf(mockBlogImageMedia(mockImageUrl, 0.5F)),
        listOf(mockBlogImageMedia(mockImageUrl, 0.1F)),
    )
}
