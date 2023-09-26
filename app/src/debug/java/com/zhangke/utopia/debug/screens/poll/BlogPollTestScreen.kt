package com.zhangke.utopia.debug.screens.poll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.ui.poll.BlogPoll

class BlogPollTestScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        Scaffold(
            topBar = {
                Toolbar(
                    title = "BlogPoll",
                    onBackClick = navigator::pop,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(start = 15.dp, end = 15.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = "Poll 1",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    poll = mockPoll(
                        listOf(
                            mockPollOption("选项一AA"),
                            mockPollOption("CCC"),
                            mockPollOption("DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD"),
                        )
                    ),
                )
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = "Poll 2",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    poll = mockPoll(
                        listOf(
                            mockPollOption("AAA", 10),
                            mockPollOption(
                                "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD",
                                190
                            ),
                        )
                    ),
                )
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = "Poll 3",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    poll = mockPoll(
                        listOf(
                            mockPollOption("BBB", 10),
                            mockPollOption(
                                "DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD",
                                10
                            ),
                            mockPollOption("CCC", 1000),
                        )
                    ),
                )
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = "Poll 4",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    poll = mockPoll(
                        listOf(
                            mockPollOption("BBB", 80),
                            mockPollOption("CCC", 36),
                        )
                    ),
                )
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Left,
                    text = "Poll 5",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    poll = mockPoll(
                        listOf(
                            mockPollOption("BBB", 0),
                            mockPollOption("CCC", 1000),
                        )
                    ),
                )
            }
        }
    }

    private fun mockPoll(options: List<BlogPoll.Option>): BlogPoll {
        return BlogPoll(
            id = System.currentTimeMillis().toString(),
            expiresAt = null,
            expired = false,
            multiple = false,
            votesCount = 124,
            votersCount = 100,
            voted = false,
            options = options,
            ownVotes = false,
        )
    }

    private fun mockPollOption(content: String, votesCount: Int = 0): BlogPoll.Option {
        return BlogPoll.Option(
            title = content,
            votesCount = votesCount,
        )
    }
}
