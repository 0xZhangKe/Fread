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

class SixfoldImageTestScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val list = remember {
            mockSixImageMediaList()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = "SixfoldImageMedia",
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
                            "${item[3].fetchDebugAspectRatio()}," +
                            "${item[4].fetchDebugAspectRatio()}," +
                            "${item[5].fetchDebugAspectRatio()},"
                    MockBlog(label) {
                        BlogMedias(
                            modifier = Modifier
                                .fillMaxWidth(),
                            mediaList = item,
                            onMediaClick = { _, _ -> },
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

private const val mockImageUrl6 =
    "https://media.cmx.edu.kg/media_attachments/files/110/922/376/853/421/606/original/c6d7bed6348ebf9f.png"

private fun mockSixImageMediaList(): List<List<BlogMedia>> {
    return listOf(
        // Grid layout
        sixfoldImageMedia(0.1F, 0.1F, 0.1F, 0.1F, 0.1F, 0.1F),
        sixfoldImageMedia(0.8F, 0.8F, 0.8F, 0.8F, 0.8F, 0.8F),
        sixfoldImageMedia(1F, 1F, 1F, 1F, 1F, 1F),
        sixfoldImageMedia(2F, 2F, 2F, 2F, 2F, 2F),
        sixfoldImageMedia(10F, 10F, 10F, 10F, 10F, 10F),
    )
}

private fun sixfoldImageMedia(
    firstAspect: Float,
    secondAspect: Float,
    thirdAspect: Float,
    fourAspect: Float,
    fiveAspect: Float,
    sixAspect: Float,
): List<BlogMedia> {
    return listOf(
        mockBlogImageMedia(mockImageUrl1, firstAspect),
        mockBlogImageMedia(mockImageUrl2, secondAspect),
        mockBlogImageMedia(mockImageUrl3, thirdAspect),
        mockBlogImageMedia(mockImageUrl4, fourAspect),
        mockBlogImageMedia(mockImageUrl5, fiveAspect),
        mockBlogImageMedia(mockImageUrl6, sixAspect),
    )
}
