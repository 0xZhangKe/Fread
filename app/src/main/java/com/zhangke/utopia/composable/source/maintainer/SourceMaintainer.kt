package com.zhangke.utopia.composable.source.maintainer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceMaintainer

@Composable
fun SourceMaintainer(
    modifier: Modifier = Modifier,
    uiState: SourceMaintainerUiState,
    onSourceOptionClick: (source: StatusSourceUiState) -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5F)
                        .background(Color.Green),
                    model = uiState.thumbnail,
                    contentScale = ContentScale.Crop,
                    contentDescription = "cover",
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x99000000)),
                            )
                        )
                        .padding(start = 15.dp, end = 15.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 5.dp),
                        text = uiState.name,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )

                    if (uiState.url.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            text = uiState.url,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                        text = uiState.description,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }
            Box(modifier = Modifier.height(10.dp))
            uiState.sourceList.forEach { blogSource ->
                Surface(
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    elevation = 5.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 10.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterStart),
                            text = blogSource.nickName,
                            fontWeight = FontWeight.Bold,
                        )
                        val optionVector = if (blogSource.selected) {
                            Icons.Outlined.Remove
                        } else {
                            Icons.Outlined.Add
                        }
                        Image(
                            modifier = Modifier
                                .size(20.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    onSourceOptionClick(blogSource)
                                },
                            imageVector = optionVector,
                            contentDescription = if (blogSource.selected) {
                                "Remove Server"
                            } else {
                                "Add server"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMaintainer() {
    val sourceList = listOf(
        StatusSourceUiState(
            uri = "mastodon.social",
            nickName = "Webb",
            description = "Webb`s Timeline",
            thumbnail = null,
            selected = false,
            onSaveToLocal = {},
            onRequestMaintainer = mockMaintainerRequester(),
        ),
        StatusSourceUiState(
            uri = "",
            nickName = "Jack Wharton",
            description = "Wharton`s Timeline",
            thumbnail = null,
            selected = true,
            onSaveToLocal = {},
            onRequestMaintainer = mockMaintainerRequester(),
        ),
    )
    val maintainer = SourceMaintainerUiState(
        url = "mastodon.social",
        name = "Mastodon Social",
        thumbnail = null,
        description = "This is Mastodon official instance.",
        sourceList = sourceList,
    )
    SourceMaintainer(
        uiState = maintainer,
        onSourceOptionClick = {},
    )
}

private class PreviewStatusSourceMaintainer(
    override val url: String = "",
    override val name: String,
    override val description: String,
    override val thumbnail: String? = null,
    override val sourceList: List<StatusSource> = emptyList(),
) : StatusSourceMaintainer

private fun mockMaintainerRequester(): suspend () -> StatusSourceMaintainer {
    return {
        PreviewStatusSourceMaintainer(
            name = "",
            description = "",
        )
    }
}