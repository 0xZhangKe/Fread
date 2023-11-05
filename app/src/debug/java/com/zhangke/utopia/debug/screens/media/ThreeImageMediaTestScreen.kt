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
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.ktx.second
import com.zhangke.framework.ktx.third
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.ui.BlogMedias

class ThreeImageMediaTestScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val list = remember {
            mockThreeImageMediaList()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = "ThreeImageMedia",
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
                    val label = "${item.first().fetchDebugAspectRatio()}, " +
                            "${item.second().fetchDebugAspectRatio()}, " +
                            "${item.third().fetchDebugAspectRatio()}"
                    MockBlog(label) {
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

private const val mockImageUrl1 =
    "https://media.cmx.edu.kg/cache/media_attachments/files/110/909/368/788/194/340/original/eebdc8ce2a725959.jpeg"

private const val mockImageUrl2 =
    "https://media.cmx.edu.kg/media_attachments/files/110/911/160/697/759/594/original/ed8731d7676b35a4.png"

private const val mockImageUrl3 =
    "https://media.cmx.edu.kg/media_attachments/files/110/913/746/721/111/726/original/a5b3ec61c6cd0fa5.jpeg"

private fun mockThreeImageMediaList(): List<List<BlogMedia>> {
    return listOf(
        // horizontal arrange
        tripleImageMedia(0.1F, 0.3F, 0.3F),
        tripleImageMedia(0.5F, 3F, 1F),
        tripleImageMedia(0.8F, 0.1F, 3F),
        tripleImageMedia(1F, 0.1F, 3F),
        // vertical arrange
        tripleImageMedia(2F, 0.1F, 3F),
        tripleImageMedia(2F, 3F, 3F),
        tripleImageMedia(2F, 0.8F, 0.8F),
        tripleImageMedia(10F, 0.1F, 0.1F),
    )
}

private fun tripleImageMedia(
    firstAspect: Float,
    secondAspect: Float,
    thirdAspect: Float,
): List<BlogMedia> {
    return listOf(
        mockBlogImageMedia(mockImageUrl1, firstAspect),
        mockBlogImageMedia(mockImageUrl2, secondAspect),
        mockBlogImageMedia(mockImageUrl3, thirdAspect),
    )
}
