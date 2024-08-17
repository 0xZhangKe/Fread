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
import com.zhangke.framework.ktx.third
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.ui.media.BlogMedias

class FiveImageMediaTestScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val list = remember {
            mockFivefoldImageMediaList()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = "FivefoldImageMedia",
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
                            "${item.third().fetchDebugAspectRatio()}," +
                            "${item[3].fetchDebugAspectRatio()}, " +
                            "${item[4].fetchDebugAspectRatio()}, "
                    MockBlog(label) {
                        BlogMedias(
                            modifier = Modifier
                                .fillMaxWidth(),
                            blogTranslationState = BlogTranslationUiState(support = false),
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

private const val mockImageUrl4 =
    "https://media.cmx.edu.kg/media_attachments/files/110/913/324/151/581/983/original/13e30ecf43a076d7.jpeg"

private const val mockImageUrl5 =
    "https://media.cmx.edu.kg/media_attachments/files/110/922/456/655/829/735/original/f03978cfab6dbc50.png"

private fun mockFivefoldImageMediaList(): List<List<BlogMedia>> {
    return listOf(
        // horizontal arrange
        fivefoldImageMedia(0.1F, 0.1F, 0.1F, 0.1F, 0.1F),
        fivefoldImageMedia(0.1F, 10F, 10F, 10F, 10F),
        fivefoldImageMedia(0.9F, 0.1F, 0.1F, 0.1F, 0.1F),
        fivefoldImageMedia(0.9F, 10F, 10F, 10F, 10F),
        fivefoldImageMedia(0.6F, 0.6F, 0.6F, 0.6F, 0.6F),
        // vertical arrange
        fivefoldImageMedia(1F, 0.1F, 0.1F, 0.1F, 0.1F),
        fivefoldImageMedia(1F, 10F, 10F, 10F, 10F),
        fivefoldImageMedia(10F, 0.1F, 0.1F, 0.1F, 0.1F),
        fivefoldImageMedia(10F, 10F, 10F, 10F, 10F),
        fivefoldImageMedia(2F, 2F, 2F, 2F, 2F),
    )
}

private fun fivefoldImageMedia(
    firstAspect: Float,
    secondAspect: Float,
    thirdAspect: Float,
    fourAspect: Float,
    fiveAspect: Float,
): List<BlogMedia> {
    return listOf(
        mockBlogImageMedia(mockImageUrl1, firstAspect),
        mockBlogImageMedia(mockImageUrl2, secondAspect),
        mockBlogImageMedia(mockImageUrl3, thirdAspect),
        mockBlogImageMedia(mockImageUrl4, fourAspect),
        mockBlogImageMedia(mockImageUrl5, fiveAspect),
    )
}
