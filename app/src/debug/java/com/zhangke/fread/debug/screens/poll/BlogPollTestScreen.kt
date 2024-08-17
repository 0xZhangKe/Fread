package com.zhangke.fread.debug.screens.poll

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.ui.poll.BlogPoll

class BlogPollTestScreen : Screen {

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
                val poll1 = mutableListOf<BlogPoll.Option>().apply {
                    for (index in 10 until 1000 step 100) {
                        add(
                            mockPollOption(
                                "选项 $index ",
                                index * index,
                            ),
                        )
                    }
                }
                val poll2 = mutableListOf<BlogPoll.Option>().apply {
                    for (index in 10 until 1000 step 100) {
                        add(
                            mockPollOption(
                                "选项xxxxxxxxxxxxxxxsssxxxxssss$index----xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx---------------",
                                index * index,
                            ),
                        )
                    }
                }
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
                    poll = mockPoll(poll1),
                    blogTranslationState = BlogTranslationUiState(support = false),
                    onVoted = {
                    }
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    textAlign = TextAlign.Left,
                    text = "Poll 2",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    blogTranslationState = BlogTranslationUiState(support = false),
                    poll = mockPoll(poll2),
                    onVoted = {
                        Log.d("U_TEST", "onVote:$it")
                    }
                )

                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp),
                    textAlign = TextAlign.Left,
                    text = "Poll 3",
                    fontSize = 18.sp,
                )
                BlogPoll(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    blogTranslationState = BlogTranslationUiState(support = false),
                    poll = mockPoll(
                        listOf(
                            mockPollOption(
                                "1",
                                1
                            ),
                            mockPollOption(
                                "30",
                                300
                            ),
                        )
                    ),
                    onVoted = {
                        Log.d("U_TEST", "onVote:$it")
                    }
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                )
            }
        }
    }

    private fun mockPoll(options: List<BlogPoll.Option>): BlogPoll {
        return BlogPoll(
            id = System.currentTimeMillis().toString(),
            expiresAt = null,
            expired = false,
            multiple = true,
            votesCount = 124,
            votersCount = 100,
            voted = true,
            options = options,
            ownVotes = listOf(0, 1),
        )
    }

    private fun mockPollOption(content: String, votesCount: Int = 0): BlogPoll.Option {
        return BlogPoll.Option(
            index = 0,
            title = content,
            votesCount = votesCount,
        )
    }
}
