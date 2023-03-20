package com.zhangke.utopia.composable.source.maintainer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.zhangke.utopia.R
import com.zhangke.utopia.status_provider.StatusSource

@Composable
fun SourceMaintainer(
    uiState: SourceMaintainerUiState,
    onAddSourceClick: (source: StatusSource) -> Unit,
    onConfirmClick: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 60.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5F),
                    model = uiState.thumbnail,
                    contentDescription = "cover"
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = uiState.url,
                    fontSize = 12.sp
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = uiState.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = uiState.description,
                    fontSize = 14.sp
                )

                Column(modifier = Modifier.padding(top = 10.dp)) {
                    uiState.sourceList.forEach { blogSource ->
                        Surface(
                            modifier = Modifier
                                .padding(start = 15.dp, end = 15.dp, bottom = 8.dp)
                                .fillMaxWidth(),
                            elevation = 5.dp,
                        ) {
                            Row(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .align(Alignment.CenterVertically),
                                    text = blogSource.nickName
                                )
                                Spacer(modifier = Modifier.weight(1F))
                                Image(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clickable {
                                            onAddSourceClick(blogSource)
                                        },
                                    imageVector = Icons.Outlined.Add,
                                    contentDescription = "Add server"
                                )
                            }
                        }
                    }
                }

                Button(
                    modifier = Modifier.padding(top = 15.dp),
                    onClick = onConfirmClick
                ) {
                    Text(text = LocalContext.current.getString(R.string.add_provider_page_confirm_button))
                }
            }
        }
    }
}